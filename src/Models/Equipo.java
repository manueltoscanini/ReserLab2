package Models;

public class Equipo {
    private int id;
    private String nombre;
    private String tipo;
    private String precauciones;

    public Equipo(int id, String nombre, String tipo, String precauciones) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precauciones = precauciones;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getPrecauciones() { return precauciones; }

    public void mostrarInfo() {
        System.out.printf("ID: %d | Nombre: %s | Tipo: %s | Precauciones: %s%n",
                id, nombre, tipo, precauciones);
    }
}