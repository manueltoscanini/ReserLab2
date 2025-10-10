package Flujos;

import DAO.*;
import Models.Actividad;
import Models.Usuario;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class UsuarioMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private final Usuario usuarioLogueado;
    private final ActividadDAO actividadDAO;
    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();

    public UsuarioMenu(Usuario usuarioLogueado) {
        this.usuarioDAO = new UsuarioDAO();
        this.clienteDAO = new ClienteDAO();
        this.actividadDAO = new ActividadDAO();
        this.usuarioLogueado = usuarioLogueado;
    }

    public void mostrarMenu() {
        int opcion = 0;
        do {
            try {
                System.out.println("\nMENÚ DE USUARIO");
                System.out.println("1. Perfil");
                System.out.println("2. Reservas");
                System.out.println("3. Equipos");
                System.out.println("4. Soporte");
                System.out.println("0. Cerrar sesión");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> mostrarSubmenuPerfil();
                    case 2 -> mostrarSubmenuReservas();
                    case 3 -> mostrarSubmenuEquipos();
                    case 4 -> mostrarSubmenuSoporte();
                    case 0 -> System.out.println("Cerrando sesión...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error en la opción: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarSubmenuPerfil() {
        int opcion = 0;
        do {
            try {
                System.out.println("\nPERFIL");
                System.out.println("1. Ver perfil");
                System.out.println("2. Editar datos personales");
                System.out.println("3. Eliminar cuenta propia");
                System.out.println("4. Cambiar contraseña");
                System.out.println("0. Volver");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> verPerfil();
                    case 2 -> editarDatos();
                    case 3 -> eliminarCuenta();
                    case 4 -> cambiarContrasenia();
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error en PERFIL: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarSubmenuReservas() {
        int opcion = 0;
        do {
            try {
                System.out.println("\nRESERVAS");
                System.out.println("1. Hacer una reserva");
                System.out.println("2. Ver disponibilidad general");
                System.out.println("3. Ver mis reservas activas");
                System.out.println("4. Cancelar reserva (24h hábiles antes)");
                System.out.println("5. Modificar reserva (24h hábiles antes)");
                System.out.println("6. Ver historial de reservas propias");
                System.out.println("7. Ver notificaciones de aprobación");
                System.out.println("8. Filtrar mis reservas por fecha");
                System.out.println("0. Volver");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> crearReserva();
                    case 2 -> verDisponibilidad();
                    case 3 -> verReservasActivas();
                    case 4 -> cancelarReserva();
                    case 5 -> modificarReserva();
                    case 6 -> verHistorialReservas();
                    case 7 -> verNotificaciones();
                    case 8 -> filtrarReservasPorFecha();
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error en RESERVAS: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarSubmenuEquipos() {
        int opcion = 0;
        do {
            try {
                System.out.println("\nEQUIPOS");
                System.out.println("1. Visualizar lista de equipos");
                System.out.println("2. Ver especificaciones de un equipo");
                System.out.println("0. Volver");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> verListaEquipos();
                    case 2 -> verEspecificacionesEquipo();
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("⚠ Error en EQUIPOS: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarSubmenuSoporte() {
        int opcion = 0;
        do {
            try {
                System.out.println("\nSOPORTE");
                System.out.println("1. Enviar consulta o reclamo al administrador");
                System.out.println("0. Volver");
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> enviarConsulta();
                    case 0 -> System.out.println("Volviendo...");
                    default -> System.out.println("Opción inválida.");
                }
            } catch (Exception e) {
                System.out.println("Error en SOPORTE: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void verPerfil() {
        try {
            System.out.println("\nDATOS DE USUARIO");
            String datosCliente = clienteDAO.obtenerDatosClientePorCedula(usuarioLogueado.getCedula());
            if (datosCliente != null) {
                System.out.println(datosCliente);
            }
        } catch (Exception e) {
            System.out.println("Error al ver perfil: " + e.getMessage());
        }
    }

    private void editarDatos() {
        try {
            System.out.println("\nEDITAR DATOS");

            System.out.print("Nuevo nombre (" + usuarioLogueado.getNombre() + "): ");
            String nuevoNombre = scanner.nextLine();
            if (!nuevoNombre.isEmpty()) {
                usuarioDAO.actualizarNombre(usuarioLogueado.getEmail(), nuevoNombre);
                usuarioLogueado.setNombre(nuevoNombre);
            }

            System.out.print("Nuevo email (" + usuarioLogueado.getEmail() + "): ");
            String nuevoEmail = scanner.nextLine();
            if (!nuevoEmail.isEmpty()) {
                if (!usuarioDAO.existeEmailExcluyendo(nuevoEmail, usuarioLogueado.getEmail())) {
                    usuarioDAO.actualizarEmail(usuarioLogueado.getEmail(), nuevoEmail);
                    usuarioLogueado.setEmail(nuevoEmail);
                } else {
                    System.out.println("El email ya está en uso, no se actualizó.");
                }
            }

            System.out.print("Nueva cédula (" + usuarioLogueado.getCedula() + "): ");
            String nuevaCedula = scanner.nextLine();
            if (!nuevaCedula.isEmpty()) {
                usuarioDAO.actualizarCedula(usuarioLogueado.getEmail(), nuevaCedula);
                usuarioLogueado.setCedula(nuevaCedula);
            }

            System.out.println("Carreras disponibles: " + clienteDAO.listarCarreras());
            System.out.print("Seleccione nueva carrera (dejar vacío para no cambiar): ");
            String carrera = scanner.nextLine();
            if (!carrera.isEmpty()) {
                Integer idCarrera = clienteDAO.obtenerIdCarreraPorNombre(carrera);
                if (idCarrera != null) {
                    clienteDAO.actualizarCarrera(idCarrera, usuarioLogueado.getCedula());
                } else {
                    System.out.println("Carrera no encontrada, no se actualizó.");
                }
            }

            System.out.println("Datos actualizados correctamente.");
        } catch (Exception e) {
            System.out.println("Error al editar datos: " + e.getMessage());
        }
    }

    private void eliminarCuenta() {
        try {
            //eliminar cuenta
            System.out.println("Cuenta eliminada.");
        } catch (Exception e) {
            System.out.println("Error al eliminar cuenta: " + e.getMessage());
        }
    }

    private void cambiarContrasenia() {
        try {
            //Cosas
        } catch (Exception e) {
            System.out.println("Error al cambiar contraseña: " + e.getMessage());
        }
    }


    private LocalTime pedirHora(String mensaje) {
        System.out.print(mensaje + ": ");
        String horaStr = scanner.nextLine().trim();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(horaStr, formato);
    }

    private void pausarPantalla() {
        System.out.println("\nPresione Enter para continuar...");
        scanner.nextLine();
    }

    private void crearReserva() {
        try {
            System.out.println("\n=== Crear reserva ===");

            System.out.print("Ingrese la fecha de la reserva (dd/MM/yyyy): ");
            String fechaStr = scanner.nextLine().trim();

            LocalDate fecha;
            try {
                DateTimeFormatter formatoUY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                fecha = LocalDate.parse(fechaStr, formatoUY);
            } catch (Exception e) {
                System.out.println("Formato de fecha inválido. Ejemplo: 07/10/2025");
                pausarPantalla();
                return;
            }

            LocalTime horaInicio = pedirHora("Hora de inicio (HH:mm)");
            LocalTime horaFin = pedirHora("Hora de fin (HH:mm)");

            LocalDate hoy = LocalDate.now();
            LocalTime ahora = LocalTime.now();

            // Validar que no sea fecha/hora pasada
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

            // Convertir a formato SQL
            java.sql.Time sqlHoraInicio = java.sql.Time.valueOf(horaInicio);
            java.sql.Time sqlHoraFin = java.sql.Time.valueOf(horaFin);

            // Validar disponibilidad
            if (actividadDAO.existeConflictoReserva(fecha, sqlHoraInicio, sqlHoraFin)) {
                System.out.println("No se puede crear la reserva: el horario ya está ocupado.");
                pausarPantalla();
                return;
            }

            System.out.print("Cedula: ");
            String cedula = scanner.nextLine().trim();

            System.out.print("Ingrese la cantidad de participantes: ");
            int cantParticipantes = Integer.parseInt(scanner.nextLine().trim());

            Integer idActividad = ActividadDAO.crearReservaYObtenerId(
                    fecha, sqlHoraInicio, sqlHoraFin, cantParticipantes, cedula, "en_espera"
            );

            if (idActividad == null) {
                System.out.println("No se pudo crear la reserva.");
                pausarPantalla();
                return;
            }

            System.out.println("Reserva creada correctamente. ID: " + idActividad);

            // Selección de equipos opcional
            var equiposDisponibles = equipoDAO.obtenerEquipos();
            java.util.Set<Integer> seleccionados = new java.util.HashSet<>();

            while (true) {
                var restantes = new java.util.ArrayList<Models.Equipo>();
                for (var eq : equiposDisponibles) {
                    if (!seleccionados.contains(eq.getId())) restantes.add(eq);
                }

                if (restantes.isEmpty()) {
                    System.out.println("No hay más equipos disponibles.");
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
                    System.out.println("Opción inválida.");
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

            System.out.println("\n✅ Reserva creada correctamente con " + seleccionados.size() + " equipos vinculados.");
            pausarPantalla();

        } catch (Exception e) {
            System.out.println("Error al crear la reserva: " + e.getMessage());
            pausarPantalla();
        }
    }

    private void verDisponibilidad() {
        try {
            List<Actividad> actividades = actividadDAO.getTodas(); // obtiene todas las reservas ordenadas por fecha

            if (actividades.isEmpty()) {
                System.out.println("\n--- Calendario de Reservas ---");
                System.out.println("No hay reservas registradas. Todas las franjas horarias disponibles.");
                return;
            }

            System.out.println("\n--- Calendario de Reservas ---");
            System.out.printf("%-12s %-8s %-8s\n",
                    "Fecha", "Inicio", "Fin");
            System.out.println("--------------------------------------------------------------------------------------");

            for (Actividad act : actividades) {

                System.out.printf("%-12s %-8s %-8s\n",
                        act.getFecha(),
                        act.getHoraInicio(),
                        act.getHoraFin());
            }
        } catch (Exception e) {
            System.out.println("Error al ver disponibilidad: " + e.getMessage());
        }
    }

    private void verReservasActivas() {
        try {
            System.out.println("Mostrando reservas activas...");
        } catch (Exception e) {
            System.out.println("Error al ver reservas activas: " + e.getMessage());
        }
    }

    private void cancelarReserva() {
        try {
            System.out.println("Cancelando reserva...");
        } catch (Exception e) {
            System.out.println("Error al cancelar reserva: " + e.getMessage());
        }
    }

    private void modificarReserva() {
        try {
            System.out.println("Modificando reserva...");
        } catch (Exception e) {
            System.out.println("Error al modificar reserva: " + e.getMessage());
        }
    }

    private void verHistorialReservas() {
        try {
            System.out.println("Historial de reservas...");
        } catch (Exception e) {
            System.out.println("Error al ver historial: " + e.getMessage());
        }
    }

    private void verNotificaciones() {
        try {
            System.out.println("Mostrando notificaciones...");
        } catch (Exception e) {
            System.out.println("Error al ver notificaciones: " + e.getMessage());
        }
    }

    private void filtrarReservasPorFecha() {
        try {
            //pedir fecha inicio y fin
            System.out.println("Filtrando reservas por fecha...");
        } catch (Exception e) {
            System.out.println("Error al filtrar reservas: " + e.getMessage());
        }
    }

    private void verListaEquipos() {
        try {
            var equipos = equipoDAO.obtenerEquipos(); // lista de objetos Equipo
            System.out.println("\n=== LISTA DE EQUIPOS ===");
            for (var equipo : equipos) {
                equipo.mostrarInfo();
            }
            System.out.println("========================\n");
        } catch (Exception e) {
            System.out.println("Error al ver lista de equipos: " + e.getMessage());
        }
    }

    private void verEspecificacionesEquipo() {
        try {
            System.out.println("Especificaciones del equipo...");
        } catch (Exception e) {
            System.out.println("Error al ver especificaciones: " + e.getMessage());
        }
    }

    private void enviarConsulta() {
        try {
            System.out.println("Enviando consulta/reclamo...");
        } catch (Exception e) {
            System.out.println("Error al enviar consulta: " + e.getMessage());
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
        }
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
            }
        }
        return fecha;
    }

    /* Método para pedir y validar la hora
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
            }
        }
        return hora;
    }*/
}