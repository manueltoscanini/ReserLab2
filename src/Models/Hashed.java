package Models;

import  org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class Hashed {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public static String encriptarContra(String contrasenia){
        return encoder.encode(contrasenia);
    }
    
    public static boolean verificarContra(String contraseniaPlana, String contraseniaEncriptada){
        return encoder.matches(contraseniaPlana, contraseniaEncriptada);
    }
}
