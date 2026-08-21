package com.demo.demo_integradora.utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailUtil {

    private static final String REMITENTE_USER;
    private static final String REMITENTE_PASS;
    private static final String HOST;
    private static final String PORT;

    static {
        String envUser = System.getenv("EMAIL_USER");
        String envPass = System.getenv("EMAIL_PASS");
        String envHost = System.getenv("EMAIL_HOST");
        String envPort = System.getenv("EMAIL_PORT");

        Properties creds = new Properties();
        try (InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream("credentials.properties")) {
            if (is != null) {
                creds.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        REMITENTE_USER = envUser != null ? envUser : creds.getProperty("email.user");
        REMITENTE_PASS = envPass != null ? envPass : creds.getProperty("email.pass");
        HOST = envHost != null ? envHost : creds.getProperty("email.host", "smtp.gmail.com");
        PORT = envPort != null ? envPort : creds.getProperty("email.port", "587");
    }

    private EmailUtil() {
    }

    public static boolean enviarCodigoRecuperacion(String destinatario, String nombreUsuario,
                                                     String codigo, String enlace) {
        String asunto = "Recuperación de contraseña - Wiki de Videojuegos";
        String cuerpo =
                "Hola " + nombreUsuario + ",\n\n" +
                "Recibimos una solicitud para recuperar el acceso a tu cuenta.\n\n" +
                "Tu código de verificación es: " + codigo + "\n\n" +
                "También puedes hacer clic en el siguiente enlace, donde deberás introducir el código:\n" +
                enlace + "\n\n" +
                "Este código vence en 15 minutos.\n" +
                "Si tú no solicitaste este cambio, puedes ignorar este correo.\n\n" +
                "— Wiki de Videojuegos";

        return enviar(destinatario, asunto, cuerpo);
    }

    /** Paso 7: correo de confirmación de que la contraseña ya se actualizó. */
    public static boolean enviarConfirmacionCambio(String destinatario, String nombreUsuario) {
        String asunto = "Tu contraseña fue actualizada - Wiki de Videojuegos";
        String cuerpo =
                "Hola " + nombreUsuario + ",\n\n" +
                "Te confirmamos que la contraseña de tu cuenta se actualizó correctamente.\n\n" +
                "Si tú no realizaste este cambio, contacta al administrador de inmediato.\n\n" +
                "— Wiki de Videojuegos";

        return enviar(destinatario, asunto, cuerpo);
    }

    private static boolean enviar(String destinatario, String asunto, String cuerpo) {
        if (REMITENTE_USER == null || REMITENTE_PASS == null) {
            System.err.println("EmailUtil: faltan credenciales de correo (email.user / email.pass " +
                    "en credentials.properties, o EMAIL_USER / EMAIL_PASS en el entorno). No se envió el correo.");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(REMITENTE_USER, REMITENTE_PASS);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(REMITENTE_USER));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            Transport.send(mensaje);
            return true;

        } catch (MessagingException e) {
            System.err.println("EmailUtil: error al enviar el correo a " + destinatario);
            e.printStackTrace();
            return false;
        }
    }
}
