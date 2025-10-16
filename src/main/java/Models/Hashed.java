package Models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Hashed {
    
    // Método simple de hash usando SHA-256 con salt
    public static String encriptarContra(String contrasenia) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Crear hash con salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(contrasenia.getBytes());
            
            // Combinar salt y hash
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            return Base64.getEncoder().encodeToString(combined);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    public static boolean verificarContra(String contraseniaPlana, String contraseniaEncriptada) {
        try {
            // Decodificar la contraseña encriptada
            byte[] combined = Base64.getDecoder().decode(contraseniaEncriptada);
            
            // Extraer salt (primeros 16 bytes)
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, 16);
            
            // Crear hash de la contraseña ingresada con el mismo salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(contraseniaPlana.getBytes());
            
            // Comparar con el hash almacenado
            byte[] storedHash = new byte[combined.length - 16];
            System.arraycopy(combined, 16, storedHash, 0, storedHash.length);
            
            return MessageDigest.isEqual(hashedPassword, storedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al verificar contraseña", e);
        }
    }
}
