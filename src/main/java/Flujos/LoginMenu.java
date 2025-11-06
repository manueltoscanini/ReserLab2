// LoginMenu.java:
package Flujos;

import DAO.UsuarioDAO;
import DAO.ClienteDAO;
import Models.Hashed;
import Models.Usuario;


import java.util.Scanner;

public class LoginMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final UsuarioDAO usuarioDAO;
    private ClienteDAO clienteDAO;

    public LoginMenu() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Login");
            System.out.println("2. Registro");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> realizarLogin();
                    case 2 -> realizarRegistro();
                    case 3 -> {
                        System.out.println("¡Hasta luego!");
                        System.exit(0);
                    }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                opcion = 0;
            }
        } while (true);
    }

    private void realizarLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Contraseña: ");
        String contrasenia = scanner.nextLine();

        try {
            Usuario usuario = usuarioDAO.autenticarUsuario(correo, contrasenia);

            if (usuario != null) {
                System.out.println("¡Login exitoso!");
                // Validar si el usuario es admin o no
                if (usuario.getEsAdmin()) {
                    System.out.println("Bienvenido, Administrador!");
                    AdministradorMenu adminMenu = new AdministradorMenu(usuario);
                    adminMenu.mostrarMenu();
                } else {
                    Usuario usuarioLogueado = usuarioDAO.autenticarUsuario(correo, contrasenia);

                    if (usuarioLogueado != null) {
                        System.out.println("Bienvenido, " + usuarioLogueado.getNombre() + "!");
                        UsuarioMenu usuarioMenu = new UsuarioMenu(usuarioLogueado);
                        usuarioMenu.mostrarMenu();
                    } else {
                        System.out.println("Email o contraseña incorrectos.");
                    }
                }
            } else {
                System.out.println("Cuenta no encontrada");
            }
        } catch (Exception e) {
            System.out.println("Error durante el login: " + e.getMessage());
        }
    }

    private void pausarPantalla() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    public void realizarRegistro() {
        System.out.println("\n=== REGISTRO ===");
        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.println("Correo: ");
        String correo = scanner.nextLine();

        // Validar formato de email
        if (!AdministradorMenu.validarEmail(correo)) {
            System.out.println("✗ El formato del email no es válido. Debe contener @ y al menos un punto (.)");
            pausarPantalla();
            return;
        }

        System.out.println("Cedula: ");
        String cedula = scanner.nextLine();

        // Validar primero que los datos no estén registrados en la base de datos
        if (usuarioDAO.existeUsuario(correo, cedula)) {
            System.out.println("Error: Ya existe un usuario con estos datos (email o cédula)");
            return;
        }

        System.out.println("Contraseña ");
        String contrasenia = scanner.nextLine();


        // Crear el usuario
        usuarioDAO.crearUsuario(nombre, correo, cedula, Hashed.encriptarContra(contrasenia));

        // Si no es admin, registrar también como cliente

            try {
                System.out.println("\n=== Registro de datos de cliente ===");
                System.out.println("Seleccione tipo de cliente:");
                System.out.println("1) estudiante");
                System.out.println("2) emprendedor");
                System.out.println("3) docente");
                System.out.println("4) invitado");
                System.out.print("Opción: ");
                int tipoOpt = Integer.parseInt(scanner.nextLine().trim());
                String tipoCliente = switch (tipoOpt) {
                    case 1 -> "estudiante";
                    case 2 -> "emprendedor";
                    case 3 -> "docente";
                    case 4 -> "invitado";
                    default -> {
                        System.out.println("Opción inválida, se asignará 'invitado'.");
                        yield "invitado";
                    }
                };

                 Integer idCarrera = null;
                 if(tipoOpt == 1) {
                     // Solo los estudiantes necesitan carrera
                     var carreras = clienteDAO.listarCarreras();
                     if (carreras == null || carreras.isEmpty()) {
                         System.out.println("No hay carreras registradas. No se pudo guardar cliente.");
                         pausarPantalla();
                         return;
                     }
                     System.out.println("Seleccione la carrera:");
                     for (int i = 0; i < carreras.size(); i++) {
                         System.out.println((i + 1) + ") " + carreras.get(i));
                     }
                     System.out.print("Opción: ");
                     int idx = Integer.parseInt(scanner.nextLine().trim());
                     if (idx < 1 || idx > carreras.size()) {
                         System.out.println("Opción inválida de carrera. No se pudo guardar cliente.");
                         pausarPantalla();
                         return;
                     }
                     String nombreCarrera = carreras.get(idx - 1);
                     idCarrera = clienteDAO.obtenerIdCarreraPorNombre(nombreCarrera);
                     if (idCarrera == null) {
                         System.out.println("Carrera no encontrada en base. No se pudo guardar cliente.");
                         pausarPantalla();
                         return;
                     }
                 } else {
                     // Para emprendedor, docente e invitado, idCarrera permanece null
                     System.out.println("Tipo de cliente: " + tipoCliente + " (sin carrera asociada)");
                 }
                 
                 // Insertar cliente con idCarrera (puede ser null para no-estudiantes)
                 boolean ok = clienteDAO.insertarCliente(cedula, tipoCliente, idCarrera);
                if (ok) {
                    System.out.println("Cliente registrado correctamente.");
                    pausarPantalla();
                } else {
                    System.out.println("No se pudo registrar el cliente.");
                    pausarPantalla();
                }
            } catch (Exception e) {
                System.out.println("Error al registrar cliente: " + e.getMessage());
                pausarPantalla();
            }

    }
}
