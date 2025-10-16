package Models;

public class Cliente extends Usuario {

    public enum tipo_cliente{Estudiante,Emprendedor,Docente,Invitado}
    public tipo_cliente tipo;

    public Cliente( tipo_cliente tipo,String nombre,String email,String cedula,String contrasenia,boolean es_admin){
        super(nombre, email, cedula, contrasenia,es_admin);
        this.tipo=tipo;
    }
    @Override
    public void mostrarInfo(){
        super.mostrarInfo();
        System.out.println("Tipo Cliente: " + this.tipo);

    }


}
