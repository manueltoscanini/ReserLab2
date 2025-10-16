package Models;

public class EquipoUso {
    private final int idEquipo;
    private final String nombre;
    private final String tipo;
    private final String uso;

    public EquipoUso(int idEquipo, String nombre, String tipo, String uso) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.uso = uso;
    }

    public int getIdEquipo() { return idEquipo; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getUso() { return uso; }
}


