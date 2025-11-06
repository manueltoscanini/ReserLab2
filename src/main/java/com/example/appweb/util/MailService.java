package com.example.appweb.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailService {

    private static final Properties props = new Properties();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static Session session;
    private static String FROM;

    static {
        try (InputStream in = MailService.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in == null) throw new IOException("No se encontró mail.properties en /resources");
            props.load(in);

            FROM = props.getProperty("app.mail.from");
            final String user = props.getProperty("app.mail.user");
            final String pass = props.getProperty("app.mail.pass");

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    System.out.println("Autenticando con usuario: " + user);
                    return new PasswordAuthentication(user, pass);
                }
            });
            
            System.out.println("Configuración de correo cargada:");
            System.out.println("  FROM: " + FROM);
            System.out.println("  USER: " + user);
            System.out.println("  HOST: " + props.getProperty("mail.smtp.host"));
            System.out.println("  PORT: " + props.getProperty("mail.smtp.port"));
        } catch (IOException e) {
            System.err.println("ERROR AL CARGAR CONFIGURACIÓN DE CORREO:");
            e.printStackTrace();
            throw new RuntimeException("Error cargando mail.properties", e);
        }
    }

    public static void sendReservationAcceptedAsync(String to, String nombre) {
        executor.submit(() -> {
            try {
                sendReservationAccepted(to, nombre);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendReservationAccepted(String to, String nombre
                                               ) throws MessagingException {
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM, false));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject("Solicitud de Reserva — ReserLab", StandardCharsets.UTF_8.name());

        String html = """
                <div style="font-family:Segoe UI,Arial,sans-serif;color:#06295a">
                  <h2 style="color:#05868a;margin:0 0 8px 0">Resultado de reserva</h2>
                  <p>Hola <b>%s</b>
                </div>
                """.formatted(nombre);

        msg.setContent(html, "text/html; charset=UTF-8");
        Transport.send(msg);
    }
    
    // En MailService.java
    public static void sendHtmlAsync(String to, String subject, String html) {
        executor.submit(() -> {
            try {
                System.out.println("Preparando para enviar correo ASÍNCRONO a: " + to);
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(FROM, false));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
                msg.setSubject(subject, StandardCharsets.UTF_8.name());
                msg.setContent(html, "text/html; charset=UTF-8");
                System.out.println("Enviando correo ASÍNCRONO...");
                Transport.send(msg);
                System.out.println("Correo ASÍNCRONO enviado exitosamente a: " + to);
            } catch (Exception e) {
                System.err.println("Error al enviar correo ASÍNCRONO a: " + to);
                e.printStackTrace();
            }
        });
    }
    
    // Método para envío síncrono (bloqueante)
    public static void sendHtmlSync(String to, String subject, String html) throws MessagingException {
        System.out.println("=== INICIO ENVÍO CORREO SÍNCRONO ===");
        System.out.println("Preparando para enviar correo SÍNCRONO a: " + to);
        System.out.println("Asunto: " + subject);
        
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM, false));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject(subject, StandardCharsets.UTF_8.name());
            msg.setContent(html, "text/html; charset=UTF-8");
            System.out.println("Enviando correo SÍNCRONO...");
            Transport.send(msg);
            System.out.println("Correo SÍNCRONO enviado exitosamente a: " + to);
        } catch (MessagingException e) {
            System.err.println("ERROR AL ENVIAR CORREO SÍNCRONO a: " + to);
            e.printStackTrace();
            throw e;
        }
        
        System.out.println("=== FIN ENVÍO CORREO SÍNCRONO ===");
    }


    public static void shutdown() {
        executor.shutdown();
    }

}