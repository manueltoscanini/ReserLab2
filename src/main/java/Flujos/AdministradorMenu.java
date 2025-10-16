package Flujos;

import DAO.*;
import Models.Actividad;
import Models.Hashed;
import Models.Sede;
import Models.Usuario;
import DAO.SedesDAO;

import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

import Models.Hashed;

public class AdministradorMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final AdministradorDAO administradorDAO;
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final SedesDAO sedesDAO = new SedesDAO();
    private final DAO.SystemLogDAO systemLogDAO = new DAO.SystemLogDAO();
    private final Usuario adminActual;
    private final CarreraDAO carreraDAO = new CarreraDAO();
    private final DateTimeFormatter formatoUY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AdministradorMenu(Usuario adminActual) {
        this.administradorDAO = new AdministradorDAO();
        this.adminActual = adminActual;
    }

    private void limpiarPantalla() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private void pausarPantalla() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }


    //listas de extras
    //verificar email al ingresar ( @  . )
    //verificar codigo de cedula  (buscar en internet)
    public void mostrarMenu() {
        try {
            int opcion;
            do {
                limpiarPantalla();
                System.out.println("--Panel de administrador--");
                System.out.println("1. Gestionar usuarios");
                System.out.println("2. Gestionar reservas");
                System.out.println("3. Gestionar equipos");
                System.out.println("4. Gestionar sedes");
                System.out.println("5. Gestionar carreras");
                System.out.println("6. Cerrar sesión");
                System.out.print("Opción: ");

                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> menuUsuarios();
                    case 2 -> menuReservas();
                    case 3 -> menuEquipos();
                    case 4 -> menuSedes();
                    case 5 -> menuCarreras();
                    case 6 -> cerrarSesion();
                    default -> System.out.println("Opción inválida");
                }
            } while (true);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuUsuarios() {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n=== Gestión de Usuarios ===");
            System.out.println("1. Crear usuario");
            System.out.println("2. Editar usuario");
            System.out.println("3. Eliminar usuario");
            System.out.println("4. Listar usuarios");
            System.out.println("5. Buscar usuario por nombre");
            System.out.println("6. Buscar usuario por email");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> crearUsuario();
                case 2 -> editarUsuario();
                case 3 -> eliminarUsuario();
                case 4 -> listarTodosLosUsuarios();
                case 5 -> buscarUsuarioPorNombre();
                case 6 -> verDetallesUsuario();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private void menuReservas() {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n=== Gestión de Reservas ===");
            System.out.println("1. Visualizar calendario general");
            System.out.println("2. Ver reservas de un usuario");
            System.out.println("3. Filtrar reservas por fecha");
            System.out.println("4. Crear reserva");
            System.out.println("5. Ver detalles de una reserva");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> visualizarCalendarioGeneral();
                case 2 -> verReservasUsuario();
                case 3 -> filtrarReservasPorFecha();
                case 4 -> crearReservaParaUsuario();
                case 5 -> verDetallesReserva();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private void menuEquipos() {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n=== Gestión de Equipos ===");
            System.out.println("1. Agregar equipo");
            System.out.println("2. Eliminar equipo");
            System.out.println("3. Editar equipo");
            System.out.println("4. Listar equipos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> agregarEquipo();
                case 2 -> eliminarEquipo();
                case 3 -> editarEquipo();
                case 4 -> listarEquipos();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private void menuSedes() {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n=== Gestión de Sedes ===");
            System.out.println("1. Crear sede");
            System.out.println("2. Visualizar sedes");
            System.out.println("3. Modificar sede");
            System.out.println("4. Eliminar sede");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> crearSedes();
                case 2 -> visualizarSedes();
                case 3 -> modificarSede();
                case 4 -> eliminarSede();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    private void menuCarreras() {
        int opcion;
        do {
            limpiarPantalla();
            System.out.println("\n=== Gestión de Carreras ===");
            System.out.println("1. Crear carrera");
            System.out.println("2. Visualizar carreras");
            System.out.println("3. Eliminar carrera");
            System.out.println("0. Volver al menú principal");
            System.out.print("Opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> crearCarrera();
                case 2 -> visualizarCarreras();
                case 3 -> eliminarCarrera();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida");
            }
        } while (opcion != 0);
    }

    // eliminar carrera
    public void eliminarCarrera() {
        System.out.println("Ingrese el ID de la carrera a eliminar: ");
        int idCarrera = Integer.parseInt(scanner.nextLine());

        boolean eliminada = carreraDAO.eliminarCarrera(idCarrera);

        if (eliminada) {
            System.out.println("Carrera eliminada correctamente.");
            pausarPantalla();
        } else {
            System.out.println("No se encontró ninguna carrera con ese ID.");
            pausarPantalla();
        }
    }

    public void crearSedes(){
        System.out.println("Nombre de la Sede: ");
        String nombre = scanner.nextLine();

        System.out.println("Direccion: ");
        String direccion = scanner.nextLine();

        System.out.println("Departamento: ");
        String departamento = scanner.nextLine();

        sedesDAO.crearSede(nombre,direccion,departamento);
    }
    //listar todos los usuarios
    public void listarTodosLosUsuarios() {
        List<Usuario> usuarios = administradorDAO.listarUsuarios();

        System.out.println("Listado de Usuarios:");
        System.out.println("Nombre\t\tEmail\t\tCedula\t\tPermisos");
        System.out.println("--------------------------------------------------------");

        for (Usuario u : usuarios) {

            if (u.getEsAdmin() == true) {
                System.out.println(u.getNombre() + "\t\t" + u.getEmail() + "\t\t" + u.getCedula() + "\t\tEs Admin");
            } else {
                System.out.println(u.getNombre() + "\t\t" + u.getEmail() + "\t\t" + u.getCedula() + "\t\tNo es Admin");
            }

        }

        pausarPantalla();
    }

    //buscar usuario por nombre
    public void buscarUsuarioPorNombre() {
        System.out.println("Escriba el nombre del usuario/s a buscar: ");
        String nombre = scanner.nextLine();

        List<Usuario> usuarios = AdministradorDAO.buscarUsuarios(nombre);

        if (usuarios.isEmpty()) {
            System.out.println("No se encontro ningun usuario con el nombre " + nombre);
            pausarPantalla();
            return;
        }
        System.out.println("Listado de Usuarios con nombre :" + nombre);
        System.out.println("Nombre\t\tEmail\t\tCedula\t\tPermisos");
        System.out.println("--------------------------------------------------------");


        for (Usuario u : usuarios) {

            if (u.getEsAdmin() == true) {
                System.out.println(u.getNombre() + "\t\t" + u.getEmail() + "\t\t" + u.getCedula() + "\t\tEs Admin");
            } else {
                System.out.println(u.getNombre() + "\t\t" + u.getEmail() + "\t\t" + u.getCedula() + "\t\tNo es Admin");
            }

        }
        pausarPantalla();

    }

    //eliminar Usuario
    public void eliminarUsuario() {
        listarTodosLosUsuarios();
        System.out.println("Email: ");
        String email = scanner.nextLine();

        if (true == administradorDAO.existeUsuario(email)) {
            System.out.println("Seguro que desea elliminar el usuario?    (s/n):");
            String seguro = scanner.nextLine();

            if ("s".equalsIgnoreCase(seguro)) {
                administradorDAO.eliminarUsuario(email);
                System.out.println("Usuario Eliminado Correctamente");
                pausarPantalla();
            }
        } else {
            System.out.println("Usuario no encontrado");
            pausarPantalla();
        }
    }


    //case 1
    public void crearUsuario() {

        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("El usuario creado es Administrador? s/n: ");
        String s_n_admin = scanner.nextLine().trim().toLowerCase();

        boolean es_admin;
        if (s_n_admin.equals("s")) {
            es_admin = true;
        } else {
            es_admin = false;
        }

        System.out.println("Correo: ");
        String correo = scanner.nextLine();

        // Validar formato de email
        if (!validarEmail(correo)) {
            System.out.println("✗ El formato del email no es válido. Debe contener @ y al menos un punto (.)");
            pausarPantalla();
            return;
        }

        // Validar que el email no esté ya registrado
        if (usuarioDAO.existeEmail(correo)) {
            System.out.println("El email ingresado ya está registrado en el sistema. Por favor, use un email diferente.");
            pausarPantalla();
            return;
        }

        System.out.println("Cedula: ");
        String cedula = scanner.nextLine();

        System.out.println("Contraseña ");
        String contrasenia = scanner.nextLine();



        AdministradorDAO.crearUsuario(nombre, correo, cedula,Hashed.encriptarContra(contrasenia), es_admin);

        // Si no es admin, registrar también como cliente
        if (!es_admin) {
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

                // Carrera
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
                Integer idCarrera = clienteDAO.obtenerIdCarreraPorNombre(nombreCarrera);
                if (idCarrera == null) {
                    System.out.println("Carrera no encontrada en base. No se pudo guardar cliente.");
                    pausarPantalla();
                    return;
                }

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




    // Validación de cédula uruguaya eliminada a pedido

    //Método para editar usuario existente
    public void editarUsuario() {
        try {
            System.out.println("\n=== LISTA DE USUARIOS ===");
            var usuarios = usuarioDAO.listarUsuarios();
            if (usuarios == null || usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados en el sistema.");
                pausarPantalla();
                return;
            }

            for (int i = 0; i < usuarios.size(); i++) {
                var usuario = usuarios.get(i);
                System.out.println((i + 1) + ". " + usuario.getNombre() + " - " + usuario.getEmail() +
                        " (Cédula: " + usuario.getCedula() + ")" +
                        (usuario.getEsAdmin() ? " [ADMIN]" : ""));
            }

            System.out.print("\nSeleccione el número del usuario que desea editar: ");
            int opcionUsuario = Integer.parseInt(scanner.nextLine().trim());
            
            if (opcionUsuario < 1 || opcionUsuario > usuarios.size()) {
                System.out.println("Opción inválida. Por favor, seleccione un número válido.");
                pausarPantalla();
                return;
            }
            
            var usuarioSeleccionado = usuarios.get(opcionUsuario - 1);


            System.out.println("\n=== INFORMACIÓN ACTUAL DEL USUARIO ===");
            usuarioSeleccionado.mostrarInfo();

            editarCamposUsuario(usuarioSeleccionado);

        } catch (Exception e) {
            System.out.println("Error al editar usuario: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void editarCamposUsuario(Models.Usuario usuario) {
        String emailActual = usuario.getEmail();
        boolean continuar = true;
        boolean esCliente = actividadDAO.existeClientePorCedula(usuario.getCedula());

        while (continuar) {
            System.out.println("\n=== ¿QUÉ CAMPO DESEA MODIFICAR? ===");
            System.out.println("1. Email");
            System.out.println("2. Nombre");
            System.out.println("3. Cédula");
            System.out.println("4. Contraseña");
            if (esCliente) {
                System.out.println("5. Carrera");
                System.out.println("6. Finalizar edición");
            } else {
                System.out.println("5. Finalizar edición");
            }
            System.out.print("Seleccione una opción: ");

            try {
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> {
                        System.out.print("Ingrese el nuevo email: ");
                        String nuevoEmail = scanner.nextLine().trim();
                        if (!nuevoEmail.isEmpty()) {
                            if (validarEmail(nuevoEmail)) {
                                if (!usuarioDAO.existeEmailExcluyendo(nuevoEmail, emailActual)) {
                                    if (usuarioDAO.actualizarEmail(emailActual, nuevoEmail)) {
                                        System.out.println("✓ Email actualizado correctamente");
                                        emailActual = nuevoEmail;
                                        pausarPantalla();
                                    } else {
                                        System.out.println("✗ Error al actualizar el email");
                                        pausarPantalla();
                                    }
                                } else {
                                    System.out.println("✗ El email ingresado ya está registrado en el sistema. Por favor, use un email diferente.");
                                    pausarPantalla();
                                }
                            } else {
                                System.out.println("✗ El formato del email no es válido. Debe contener @ y al menos un punto (.)");
                                pausarPantalla();
                            }
                        } else {
                            System.out.println("El email no puede estar vacío");
                            pausarPantalla();
                        }
                    }
                    case 2 -> {
                        System.out.print("Ingrese el nuevo nombre: ");
                        String nuevoNombre = scanner.nextLine().trim();
                        if (!nuevoNombre.isEmpty()) {
                            if (usuarioDAO.actualizarNombre(emailActual, nuevoNombre)) {
                                System.out.println("✓ Nombre actualizado correctamente");
                                pausarPantalla();
                            } else {
                                System.out.println("✗ Error al actualizar el nombre");
                                pausarPantalla();
                            }
                        } else {
                            System.out.println("El nombre no puede estar vacío");
                            pausarPantalla();
                        }
                    }
                    case 3 -> {
                        System.out.print("Ingrese la nueva cédula: ");
                        String nuevaCedula = scanner.nextLine().trim();
                        if (!nuevaCedula.isEmpty()) {
                            if (usuarioDAO.actualizarCedula(emailActual, nuevaCedula)) {
                                System.out.println("✓ Cédula actualizada correctamente");
                                pausarPantalla();
                            } else {
                                System.out.println("✗ Error al actualizar la cédula");
                                pausarPantalla();
                            }
                        } else {
                            System.out.println("La cédula no puede estar vacía");
                            pausarPantalla();
                        }
                    }
                    case 4 -> {
                        System.out.print("Ingrese la nueva contraseña: ");
                        String nuevaContrasenia = scanner.nextLine().trim();
                        if (!nuevaContrasenia.isEmpty()) {
                            if (usuarioDAO.actualizarContrasenia(emailActual,Hashed.encriptarContra(nuevaContrasenia))) {
                                System.out.println("✓ Contraseña actualizada correctamente");
                                pausarPantalla();
                            } else {
                                System.out.println("✗ Error al actualizar la contraseña");
                                pausarPantalla();
                            }
                        } else {
                            System.out.println("La contraseña no puede estar vacía");
                            pausarPantalla();
                        }
                    }
                    case 5 -> {
                        if (esCliente) {
                            editarCarreraUsuario(usuario.getCedula());
                        } else {
                            continuar = false;
                            System.out.println("Edición finalizada");
                            pausarPantalla();
                        }
                    }
                    case 6 -> {
                        if (esCliente) {
                            continuar = false;
                            System.out.println("Edición finalizada");
                            pausarPantalla();
                        } else {
                            System.out.println("Opción inválida. Intente nuevamente.");
                            pausarPantalla();
                        }
                    }
                    default -> {
                        System.out.println("Opción inválida. Intente nuevamente.");
                        pausarPantalla();
                    }
                }

                if (opcion >= 1 && opcion <= 4) {
                    System.out.println("\n=== INFORMACIÓN ACTUALIZADA ===");
                    var usuarioActualizado = usuarioDAO.buscarUsuarioPorEmail(emailActual);
                    if (usuarioActualizado != null) {
                        usuarioActualizado.mostrarInfo();
                    }
                    pausarPantalla();
                } else if (opcion == 5 && esCliente) {
                    System.out.println("\n=== INFORMACIÓN ACTUALIZADA ===");
                    var usuarioActualizado = usuarioDAO.buscarUsuarioPorEmail(emailActual);
                    if (usuarioActualizado != null) {
                        usuarioActualizado.mostrarInfo();
                    }
                    pausarPantalla();
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                pausarPantalla();
            }
        }
    }

    public static boolean validarEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        boolean tieneArroba = email.contains("@");
        boolean tienePunto = email.contains(".");

        boolean arrobaValida = email.indexOf("@") > 0 && email.lastIndexOf("@") < email.length() - 1;
        boolean puntoValido = email.indexOf(".") > 0 && email.lastIndexOf(".") < email.length() - 1;

        int posicionArroba = email.indexOf("@");
        int ultimoPunto = email.lastIndexOf(".");
        boolean formatoValido = posicionArroba < ultimoPunto && ultimoPunto - posicionArroba > 1;

        return tieneArroba && tienePunto && arrobaValida && puntoValido && formatoValido;
    }


    // cálculo de dígito verificador removido a pedido

    private void verDetallesUsuario() {
        System.out.print("Ingrese el correo del usuario: ");
        String email = scanner.nextLine();

        Usuario usuario = usuarioDAO.obtenerUsuarioPorEmail(email);

        if (usuario != null) {
            usuario.mostrarInfo();
            pausarPantalla();
        } else {
            System.out.println("Usuario no encontrado.");
            pausarPantalla();
        }
    }

    private void agregarEquipo() {
        System.out.print("Ingrese el nombre del equipo: ");
        String nombre = scanner.nextLine();

        System.out.print("Ingrese el tipo del equipo: ");
        String tipo = scanner.nextLine();

        System.out.print("Ingrese las precauciones: ");
        String precauciones = scanner.nextLine();

        equipoDAO.agregarEquipo(nombre, tipo, precauciones);
    }

    private void eliminarEquipo() {
        System.out.print("Ingrese el ID del equipo a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        equipoDAO.eliminarEquipo(id);
    }

    private void listarEquipos() {
        try {
            var equipos = equipoDAO.obtenerEquipos(); // lista de objetos Equipo
            System.out.println("\n=== LISTA DE EQUIPOS ===");
            for (var equipo : equipos) {
                equipo.mostrarInfo();
            }
            System.out.println("========================\n");
            pausarPantalla();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al listar equipos.");
            pausarPantalla();
        }
    }
    private void editarEquipo() {
        try {
            listarEquipos(); // primero mostramos la lista actual
            System.out.print("Ingrese el ID del equipo a editar: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Ingrese el nuevo nombre del equipo: ");
            String nombre = scanner.nextLine();

            System.out.print("Ingrese el nuevo tipo del equipo: ");
            String tipo = scanner.nextLine();

            System.out.print("Ingrese las nuevas precauciones: ");
            String precauciones = scanner.nextLine();

            boolean actualizado = equipoDAO.editarEquipo(id, nombre, tipo, precauciones);
            if (actualizado) {
                System.out.println("Equipo modificado correctamente.");
                pausarPantalla();
            }
        } catch (Exception e) {
            System.out.println("Error al editar equipo: " + e.getMessage());
            pausarPantalla();
        }
    }



    private void cerrarSesion() {
        System.out.println("¿Está seguro que desea cerrar sesión? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();

        if (confirmacion.equals("s")) {
            System.out.println("Sesión cerrada. Redirigiendo al login...");
            LoginMenu loginMenu = new LoginMenu();
            loginMenu.mostrarMenuPrincipal();
        } else {
            System.out.println("Operación cancelada, continúa en la sesión actual.");
            pausarPantalla();
        }
    }

    private void crearReservaParaUsuario() {
        try {
            System.out.println("\n=== Crear reserva directa===");

            var usuarios = usuarioDAO.listarUsuarios();
            if (usuarios == null || usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados");
                pausarPantalla();
                return;
            }

            System.out.println("Seleccione el usuario:");
            for (int i = 0; i < usuarios.size(); i++) {
                var u = usuarios.get(i);
                System.out.println((i + 1) + ") " + u.getNombre() + " - " + u.getEmail() + (u.getEsAdmin() ? " [ADMIN]" : ""));
            }
            System.out.print("Opción: ");
            int idxUsuario = Integer.parseInt(scanner.nextLine().trim());
            if (idxUsuario < 1 || idxUsuario > usuarios.size()) {
                System.out.println("Opción inválida");
                pausarPantalla();
                return;
            }

            var usuario = usuarios.get(idxUsuario - 1);
            if (usuario.getEsAdmin()) {
                System.out.println("No se puede asignar reservas a administradores");
                pausarPantalla();
                return;
            }

            String cedulaCliente = usuario.getCedula();
            if (!actividadDAO.existeClientePorCedula(cedulaCliente)) {
                System.out.println("El usuario no está registrado como cliente. Regístrelo en tabla cliente.");
                pausarPantalla();
                return;
            }

            // Uso de métodos auxiliares
            LocalDate fecha = pedirFecha("Ingrese la fecha de la reserva");
            LocalTime horaInicio = pedirHora("Hora inicio");
            LocalTime horaFin = pedirHora("Hora fin");

            LocalDate hoy = LocalDate.now();
            LocalTime ahora = LocalTime.now();

            if (fecha.isBefore(hoy) || (fecha.isEqual(hoy) && horaInicio.isBefore(ahora))) {
                System.out.println("La fecha y hora de inicio no pueden ser anteriores al momento actual.");
                pausarPantalla();
                return;
            }

            if (horaFin.isBefore(horaInicio)) {
                System.out.println("La hora de fin no puede ser anterior a la de inicio.");
                pausarPantalla();
                return;
            }

            System.out.print("Cantidad de participantes: ");
            int cant = Integer.parseInt(scanner.nextLine().trim());

            java.sql.Time sqlHoraInicio = java.sql.Time.valueOf(horaInicio);
            java.sql.Time sqlHoraFin = java.sql.Time.valueOf(horaFin);

            if (actividadDAO.existeConflictoReserva(fecha, sqlHoraInicio, sqlHoraFin)) {
                System.out.println("No se puede crear la reserva: el horario ya está ocupado.");
                pausarPantalla();
                return;
            }

            Integer idActividad = actividadDAO.crearReservaYObtenerId(
                    fecha, sqlHoraInicio, sqlHoraFin, cant, cedulaCliente, "aceptada"
            );

            if (idActividad == null) {
                System.out.println("No se pudo crear la reserva.");
                pausarPantalla();
                return;
            }

            String detalleLog = "Reserva creada desde panel administrador para ci " + cedulaCliente;
            try {
                systemLogDAO.registrarAccion(
                        "creación", idActividad, adminActual != null ? adminActual.getEmail() : null, detalleLog
                );
                pausarPantalla();
            } catch (Exception ex) {
                System.out.println("Advertencia: no se pudo registrar el log del sistema: " + ex.getMessage());
                pausarPantalla();
            }

            // Selección de equipos
            var equiposDisponibles = equipoDAO.obtenerEquipos();
            java.util.Set<Integer> seleccionados = new java.util.HashSet<>();
            while (true) {
                var restantes = new java.util.ArrayList<Models.Equipo>();
                for (var eq : equiposDisponibles) {
                    if (!seleccionados.contains(eq.getId())) restantes.add(eq);
                }
                if (restantes.isEmpty()) {
                    System.out.println("No hay más equipos para seleccionar.");
                    pausarPantalla();
                    break;
                }
                System.out.println("\nSeleccione equipos para vincular (0 = continuar):");
                for (int i = 0; i < restantes.size(); i++) {
                    var eq = restantes.get(i);
                    System.out.println((i + 1) + ") " + eq.getNombre() + " (" + eq.getTipo() + ")");
                }
                System.out.print("Opción: ");
                int opt = Integer.parseInt(scanner.nextLine().trim());
                if (opt == 0) break;
                if (opt < 1 || opt > restantes.size()) {
                    System.out.println("Opción inválida");
                    pausarPantalla();
                    continue;
                }
                var elegido = restantes.get(opt - 1);
                System.out.print("Describa el uso del equipo (opcional): ");
                String uso = scanner.nextLine().trim();
                boolean ok = actividadDAO.vincularEquipoAActividad(idActividad, elegido.getId(), uso);
                if (ok) {
                    seleccionados.add(elegido.getId());
                    System.out.println("Equipo agregado.");
                } else {
                    System.out.println("No se pudo agregar el equipo.");
                }
            }

            System.out.println("Reserva creada y confirmada correctamente. Equipos vinculados: " + seleccionados.size());
            pausarPantalla();

        } catch (Exception e) {
            System.out.println("Error al crear la reserva: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void visualizarSedes() {
        List<Sede> sedes = sedesDAO.obtenerSedes();
        if (sedes.isEmpty()) {
            System.out.println("No hay sedes disponibles.");
            pausarPantalla();
            return;
        }
        System.out.println("=== Sedes Registradas ===");
        for (Sede s : sedes) {
            System.out.println("Nombre: " + s.getNombre());
            System.out.println("Dirección: " + s.getDireccion());
            System.out.println("Departamento: " + s.getDepartamento());
            System.out.println("---------------------------");
        }
        pausarPantalla();
    }

    private void crearCarrera() {
        List<Sede> sedes = sedesDAO.obtenerSedes();
        if (sedes.isEmpty()) {
            System.out.println("No hay sedes registradas. Cree una sede primero.");
            pausarPantalla();
            return;
        }
        System.out.print("Ingrese nombre de la carrera: ");
        String nombreCarrera = scanner.nextLine().trim();
        if (nombreCarrera.isEmpty()) {
            System.out.println("Nombre inválido");
            pausarPantalla();
            return;
        }

        System.out.println("Seleccione la sede:");
        for (int i = 0; i < sedes.size(); i++) {
            System.out.println((i+1) + ") " + sedes.get(i).getNombre());
        }
        System.out.print("Opción: ");
        int idx = Integer.parseInt(scanner.nextLine().trim());
        if (idx < 1 || idx > sedes.size()) {
            System.out.println("Opción inválida");
            pausarPantalla();
            return;
        }

        int idSede = idx;
        boolean ok = new CarreraDAO().crearCarrera(nombreCarrera, idSede);
        if (ok) {
            System.out.println("Carrera creada correctamente.");
            pausarPantalla();
        }
        else {
            System.out.println("Ya existe una carrera con ese nombre en la misma sede.");
            pausarPantalla();
        }
    }

    private void visualizarCarreras() {
        List<String> carreras = new CarreraDAO().obtenerCarrerasConSede();
        if (carreras.isEmpty()) {
            System.out.println("No hay carreras disponibles.");
            pausarPantalla();
            return;
        }
        System.out.println("=== Carreras Registradas ===");
        carreras.forEach(System.out::println);
        pausarPantalla();
    }

    private void filtrarReservasPorFecha() {
        try {
            LocalDate fecha = pedirFecha("Ingrese la fecha a filtrar");
            DateTimeFormatter formatoUY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            List<Actividad> reservas = actividadDAO.getPorFecha(fecha);
            if (reservas.isEmpty()) {
                System.out.println("Sin resultados para la búsqueda.");
                pausarPantalla();
                return;
            }

            System.out.println("=== Reservas para " + fecha.format(formatoUY) + " ===");
            for (Actividad a : reservas) {
                a.mostrarInfo();
                System.out.println("-----------------------");
            }
            pausarPantalla();
        } catch (Exception e) {
            System.out.println("Error al filtrar por fecha: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void verDetallesReserva() {
        try {
            var reservas = actividadDAO.getTodas();
            if (reservas == null || reservas.isEmpty()) {
                System.out.println("No hay reservas registradas.");
                pausarPantalla();
                return;
            }

            java.time.format.DateTimeFormatter formatoUY = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

            System.out.println("\n=== Seleccione una reserva ===");
            for (int i = 0; i < reservas.size(); i++) {
                var r = reservas.get(i);
                System.out.println((i + 1) + ") ID:" + r.getIdActividad()
                        + " | Fecha:" + r.getFecha().format(formatoUY)
                        + " " + r.getHoraInicio() + "-" + r.getHoraFin()
                        + " | Estado:" + r.getEstado());
            }

            System.out.print("Opción: ");
            int idx = Integer.parseInt(scanner.nextLine().trim());
            if (idx < 1 || idx > reservas.size()) {
                System.out.println("Opción inválida.");
                pausarPantalla();
                return;
            }

            var seleccionada = reservas.get(idx - 1);
            var act = actividadDAO.obtenerActividadPorId(seleccionada.getIdActividad());
            if (act == null) {
                System.out.println("No se encontró la actividad seleccionada.");
                pausarPantalla();
                return;
            }

            System.out.println("\n=== Detalles de la reserva ===");
            act.mostrarInfo();

//            // Datos del cliente
//            String datosCliente = clienteDAO.obtenerDatosClientePorCedula(act.getCedulaCliente());
//            if (datosCliente != null) {
//                System.out.println("\nCliente: " + datosCliente);
//            }

            var equipos = actividadDAO.obtenerEquiposDeActividad(act.getIdActividad());
            System.out.println("\nEquipos vinculados (" + (equipos != null ? equipos.size() : 0) + "):");
            if (equipos != null && !equipos.isEmpty()) {
                for (var eu : equipos) {
                    System.out.println("- [" + eu.getNombre() + "] Tipo: " + eu.getTipo()
                            + (eu.getUso() != null && !eu.getUso().isEmpty() ? " | Uso: " + eu.getUso() : ""));
                }
            } else {
                System.out.println("(Sin equipos vinculados)");
            }
            pausarPantalla();
        } catch (Exception e) {
            System.out.println("Error al mostrar detalles de la reserva: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void eliminarSede() {
        try {
            // listar sedes (suponiendo que sedesDAO.listarSedes() existe y devuelve List<Models.Sede>)
            var sedes = sedesDAO.obtenerSedes();
            if (sedes == null || sedes.isEmpty()) {
                System.out.println("No hay sedes registradas.");
                pausarPantalla();
                return;
            }

            System.out.println("=== Sedes disponibles ===");
            for (int i = 0; i < sedes.size(); i++) {
                System.out.println((i + 1) + ") " + sedes.get(i).getNombre());
            }

            System.out.print("Ingrese el número o nombre de la sede a eliminar: ");
            String entrada = scanner.nextLine().trim();

            String nombre;
            try {
                int opt = Integer.parseInt(entrada);
                if (opt < 1 || opt > sedes.size()) {
                    System.out.println("Opción inválida.");
                    pausarPantalla();
                    return;
                }
                nombre = sedes.get(opt - 1).getNombre();
            } catch (NumberFormatException ex) {
                nombre = entrada;
            }

            System.out.print("¿Confirma eliminar la sede '" + nombre + "'? (s/n): ");
            String conf = scanner.nextLine().trim().toLowerCase();
            if (!conf.equals("s")) {
                System.out.println("Operación cancelada.");
                pausarPantalla();
                return;
            }

            boolean ok = sedesDAO.eliminarSedePorNombre(nombre);
            if (!ok) {
                System.out.println("No se pudo eliminar la sede.");
                pausarPantalla();
            }
            pausarPantalla();
        } catch (Exception e) {
            System.out.println("Error al eliminar sede: " + e.getMessage());
            e.printStackTrace(); // útil para debug
            pausarPantalla();
        }
    }

    public void modificarSede() {
        System.out.println("=== MODIFICAR SEDE ===");

        System.out.print("Ingrese el nombre de la sede a modificar: ");
        String nombreActual = scanner.nextLine();

        System.out.print("Ingrese el nuevo nombre: ");
        String nuevoNombre = scanner.nextLine();

        System.out.print("Ingrese la nueva dirección: ");
        String nuevaDireccion = scanner.nextLine();

        System.out.print("Ingrese el nuevo departamento: ");
        String nuevoDepartamento = scanner.nextLine();

        boolean resultado = sedesDAO.modificarSede(nombreActual, nuevoNombre, nuevaDireccion, nuevoDepartamento);

        if (resultado) {
            System.out.println("La sede fue modificada exitosamente.");
            pausarPantalla();
        } else {
            System.out.println("No se pudo modificar la sede (nombre inexistente).");
            pausarPantalla();
        }
    }


    private void visualizarCalendarioGeneral() {
        try {
            List<Actividad> actividades = actividadDAO.getTodas(); // obtiene todas las reservas ordenadas por fecha

            if (actividades.isEmpty()) {
                System.out.println("\n--- Calendario de Reservas ---");
                System.out.println("No hay reservas registradas. Todas las franjas horarias disponibles.");
                pausarPantalla();
                return;
            }

            System.out.println("\n--- Calendario de Reservas ---");
            System.out.printf("%-5s %-12s %-8s %-8s %-10s %-20s %-15s %-10s\n",
                    "ID", "Fecha", "Inicio", "Fin", "Estado", "Cliente", "Carrera", "Participantes");
            System.out.println("--------------------------------------------------------------------------------------");

            for (Actividad act : actividades) {

                System.out.printf("%-5d %-12s %-8s %-8s %-10s %-20s %-15s %-10d\n",
                        act.getIdActividad(),
                        act.getFecha(),
                        act.getHoraInicio(),
                        act.getHoraFin(),
                        act.getEstado(),
                        act.getNombreCliente(),
                        act.getCarreraCliente(),
                        act.getCantidadParticipantes());

            }
            pausarPantalla();
        } catch (Exception e) {
            System.out.println("Error al cargar el calendario: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void verReservasUsuario() {
        listarTodosLosUsuarios();
        System.out.print("Ingrese la cédula del usuario: ");
        String cedula = scanner.nextLine().trim();

        List<Actividad> reservas = actividadDAO.getHistorialPorUsuario(cedula);

        if (reservas == null || reservas.isEmpty()) {
            System.out.println("El usuario no tiene reservas.");
            pausarPantalla();
            return;
        }

        System.out.println("=== Reservas del usuario ===");
        for (Actividad r : reservas) {
            r.mostrarInfo();
            System.out.println("-----------------------");
        }
        pausarPantalla();
    }

    // Método para pedir y validar la fecha
    private LocalDate pedirFecha(String mensaje) {
        DateTimeFormatter formatoUY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = null;
        while (fecha == null) {
            try {
                System.out.print(mensaje + " (dd/MM/yyyy): ");
                String fechaStr = scanner.nextLine().trim();
                fecha = LocalDate.parse(fechaStr, formatoUY);
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida. Intente nuevamente.");
                pausarPantalla();
            }
        }
        return fecha;
    }

    // Método para pedir y validar la hora
    private LocalTime pedirHora(String mensaje) {
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime hora = null;
        while (hora == null) {
            try {
                System.out.print(mensaje + " (HH:mm): ");
                String horaStr = scanner.nextLine().trim();
                hora = LocalTime.parse(horaStr, formatoHora);
            } catch (DateTimeParseException e) {
                System.out.println("Hora inválida. Intente nuevamente.");
                pausarPantalla();
            }
        }
        return hora;
    }

    private void editarCarreraUsuario(String cedulaCliente) {
        try {
            System.out.println("\n=== CARRERAS DISPONIBLES ===");
            var carreras = clienteDAO.listarCarreras();
            if (carreras.isEmpty()) {
                System.out.println("No hay carreras disponibles.");
                pausarPantalla();
                return;
            }

            for (int i = 0; i < carreras.size(); i++) {
                System.out.println((i + 1) + ". " + carreras.get(i));
            }

            System.out.print("\nSeleccione el número de la nueva carrera: ");
            int opcionCarrera = Integer.parseInt(scanner.nextLine().trim());
            
            if (opcionCarrera < 1 || opcionCarrera > carreras.size()) {
                System.out.println("Opción inválida. Por favor, seleccione un número válido.");
                pausarPantalla();
                return;
            }
            
            String nombreCarrera = carreras.get(opcionCarrera - 1);
            Integer idCarrera = clienteDAO.obtenerIdCarreraPorNombre(nombreCarrera);
            
            if (idCarrera != null) {
                clienteDAO.actualizarCarrera(idCarrera, cedulaCliente);
                System.out.println("✓ Carrera actualizada correctamente a: " + nombreCarrera);
            } else {
                System.out.println("✗ Error al obtener el ID de la carrera");
            }
            pausarPantalla();
        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
            pausarPantalla();
        } catch (Exception e) {
            System.out.println("Error al editar carrera: " + e.getMessage());
            pausarPantalla();
        }
    }
}