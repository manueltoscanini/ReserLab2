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
                    return new PasswordAuthentication(user, pass);
                }
            });
        } catch (IOException e) {
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
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(FROM, false));
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
                msg.setSubject(subject, StandardCharsets.UTF_8.name());
                msg.setContent(html, "text/html; charset=UTF-8");
                Transport.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public static void shutdown() {
        executor.shutdown();
    }

}

