package Models;

import java.sql.Time;
import java.time.LocalDate;

public class Actividad {
    private int idActividad;
    private LocalDate fecha;
    private Time horaInicio;
    private Time horaFin;
    private String estado;
    private int cantidadParticipantes;
    private String cedulaCliente;
    private String nombreCliente;
    private String carreraCliente;

    public Actividad(int idActividad,
                     LocalDate fecha,
                     Time horaInicio,
                     Time horaFin,
                     String estado,
                     int cantidadParticipantes,
                     String cedulaCliente,
                     String nombreCliente,
                     String carreraCliente) {
        this.idActividad = idActividad;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.cantidadParticipantes = cantidadParticipantes;
        this.cedulaCliente = cedulaCliente;
        this.nombreCliente = nombreCliente;
        this.carreraCliente = carreraCliente;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Time getHoraInicio() {
        return horaInicio;
    }

    public Time getHoraFin() {
        return horaFin;
    }

    public String getEstado() {
        return estado;
    }

    /**
     * Obtiene la cantidad de participantes de la actividad.
     * @return Número de participantes
     */
    public int getCantidadParticipantes() {
        return cantidadParticipantes;
    }

    /**
     * Alias para getCantidadParticipantes().
     * Se usa para compatibilidad con JSON cuando se serializa/deserializa.
     * Gson y otras librerías JSON buscan este nombre de método.
     * @return Número de participantes
     */
    public int getCantParticipantes() {
        return cantidadParticipantes;
    }

    /**
     * Obtiene la cédula del cliente dueño de la reserva.
     * @return Cédula del cliente
     */
    public String getCedulaCliente() {
        return cedulaCliente;
    }

    /**
     * Alias para getCedulaCliente().
     * Se usa para compatibilidad con JSON cuando se serializa/deserializa.
     * Gson busca este nombre de método al convertir a JSON.
     * IMPORTANTE: Este método es usado por el servlet de edición para validar permisos.
     * @return Cédula del cliente
     */
    public String getCiCliente() {
        return cedulaCliente;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getNombreCliente() { return nombreCliente; }
    public String getCarreraCliente() { return carreraCliente; }


    public void mostrarInfo() {
        java.time.format.DateTimeFormatter formatoUY = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.println("=== Actividad ===");
        System.out.println("ID: " + idActividad);
        System.out.println("Fecha: " + fecha.format(formatoUY));
        System.out.println("Inicio: " + horaInicio + " - Fin: " + horaFin);
        System.out.println("Estado: " + estado);
        System.out.println("Participantes: " + cantidadParticipantes);
        System.out.println("Cliente: " + nombreCliente + " (" + cedulaCliente + ")");
        System.out.println("Carrera: " + carreraCliente);

    }
}


