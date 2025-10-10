package Models;

public class Administrador extends Usuario {

    public Administrador(String nombre,String email,String cedula,String contrasenia,boolean es_admin){
        super(nombre, email, cedula, contrasenia,es_admin);

    }
    @Override
    public void mostrarInfo(){
        super.mostrarInfo();

    }

}
