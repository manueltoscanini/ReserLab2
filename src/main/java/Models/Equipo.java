package Models;

public class Equipo {
    private int id;
    private String nombre;
    private String tipo;
    private String precauciones;
    private String foto_equipo;

    public Equipo(int id, String nombre, String tipo, String precauciones,String foto_equipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precauciones = precauciones;
        this.foto_equipo = foto_equipo;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getPrecauciones() { return precauciones; }
    public String getFoto_Equipo(){return foto_equipo; }

    public void mostrarInfo() {
        System.out.printf("ID: %d | Nombre: %s | Tipo: %s | Precauciones: %s%n",
                id, nombre, tipo, precauciones);
    }
}