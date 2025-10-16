package com.example.appweb;

import Models.Hashed;

/**
 * Clase para generar contraseñas hasheadas
 * Ejecutar este main para obtener los hashes correctos para la base de datos
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        System.out.println("=== Generador de Contraseñas Hasheadas ===");
        System.out.println();
        
        // Generar hash para admin123
        String adminPassword = "admin123";
        String adminHash = Hashed.encriptarContra(adminPassword);
        System.out.println("Contraseña: " + adminPassword);
        System.out.println("Hash: " + adminHash);
        System.out.println("SQL: UPDATE usuario SET contrasenia = '" + adminHash + "' WHERE email = 'admin@test.com';");
        System.out.println();
        
        // Generar hash para user123
        String userPassword = "user123";
        String userHash = Hashed.encriptarContra(userPassword);
        System.out.println("Contraseña: " + userPassword);
        System.out.println("Hash: " + userHash);
        System.out.println("SQL: UPDATE usuario SET contrasenia = '" + userHash + "' WHERE email = 'user@test.com';");
        System.out.println();
        
        // Verificar que los hashes funcionen
        System.out.println("=== Verificación ===");
        System.out.println("Admin123 verificado: " + Hashed.verificarContra(adminPassword, adminHash));
        System.out.println("User123 verificado: " + Hashed.verificarContra(userPassword, userHash));
    }
}

