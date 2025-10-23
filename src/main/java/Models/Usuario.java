//Usuario.java:
package Models;

public class Usuario {
    private String nombre,email,cedula,contrasenia;
    private boolean es_admin;
    private String fotoUsuario;


    public Usuario(String nombre,String email,String cedula,String contrasenia,boolean es_admin ){

        this.nombre=nombre;
        this.email=email;
        this.cedula=cedula;
        this.contrasenia=contrasenia;
        this.es_admin=es_admin;


    }

    public Usuario(String nombre,String email,String cedula,String contrasenia,boolean es_admin, String fotoUsuario){
        this.nombre=nombre;
        this.email=email;
        this.cedula=cedula;
        this.contrasenia=contrasenia;
        this.es_admin=es_admin;
        this.fotoUsuario=fotoUsuario;
    }
    public Usuario(){
        this.nombre=null;
        this.email=null;
        this.cedula=null;
        this.contrasenia=null;
        this.es_admin= Boolean.parseBoolean(null);

    }

    public String getNombre(){
        return this.nombre;
    }
    public String getEmail(){
        return this.email;
    }
    public String getCedula(){
        return this.cedula;
    }
    public String getContrasenia(){
        return this.contrasenia;
    }
    public boolean getEsAdmin(){
        return this.es_admin;
    }
    public String getFotoUsuario() {
        return this.fotoUsuario;
    }
    public boolean isEsadmin(){
        return this.es_admin;
    }


    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setEsadmin(Boolean es_admin){this.es_admin=es_admin;}
    public void setCedula(String cedula) {this.cedula=cedula;}
    public void setContrasenia(String contrasenia) {this.contrasenia=contrasenia;}
    public void setFotoUsuario(String fotoUsuario) {this.fotoUsuario=fotoUsuario;}

    public void mostrarInfo(){
        System.out.println("-------------------------");
        System.out.println("Nombre: " + this.nombre);
        System.out.println("Email: " + this.email);
        System.out.println("Cedula " + this.cedula);

    }
}
