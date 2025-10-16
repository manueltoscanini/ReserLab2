package Models;

import java.util.ArrayList;
import java.util.List;

public class Sede {
    private String nombre,direccion,departamento;
    private List<Carrera> carreras;

    public Sede(String nombre,String direccion,String departamento){
        this.nombre=nombre;
        this.direccion=direccion;
        this.departamento=departamento;
        this.carreras= new ArrayList<>();
    }

    public void setNombre(String nombre){this.nombre=nombre;}
    public void setDireccion(String direccion){this.direccion=direccion;}
    public void setDepartamento(String departamento){this.departamento=departamento;}
    public void setCarrera(List<Carrera> carreras){this.carreras=carreras;}

    public String getNombre(){return nombre;}
    public String getDireccion(){return direccion;}
    public String getDepartamento(){return departamento;}
    public List<Carrera> getCarreras(){return carreras;}

    public void mostrarInfo(){
        System.out.println("-------------------------");
        System.out.println("Nombre: " + this.nombre);
        System.out.println("Direccion: " + this.direccion);
        System.out.println("Departamento " + this.departamento);
        System.out.println("Carreras: ");

        if(carreras != null && !carreras.isEmpty()){
            for (Carrera c : carreras){
                System.out.println("- " + c.getNombre()); // mostrar el nombre real de la carrera
            }
        } else {
            System.out.println("No hay carreras registradas");
        }
    }
}
