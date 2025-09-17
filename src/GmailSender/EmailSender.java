package GmailSender;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class EmailSender {
    private final Properties props = new Properties();
    private final String username;
    private final String password;

    public EmailSender() throws IOException {
        try (FileInputStream fis = new FileInputStream("resources/config.properties")) {
            props.load(fis);
        }
        username = props.getProperty("mail.username");
        password = props.getProperty("mail.password");
    }

    public void sendEmail(String to, String cc, String bcc,
                          String subject, String body,
                          List<File> attachments) throws MessagingException {

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));

        if (!to.isEmpty()) message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        if (!cc.isEmpty()) message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        if (!bcc.isEmpty()) message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));

        message.setSubject(subject);

        // Body part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        // Attachments
        if (attachments != null) {
            for (File file : attachments) {
                MimeBodyPart attachPart = new MimeBodyPart();
                try {
                    attachPart.attachFile(file);
                    multipart.addBodyPart(attachPart);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        message.setContent(multipart);
        Transport.send(message);
    }
}
