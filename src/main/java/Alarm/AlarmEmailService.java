package Alarm;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class AlarmEmailService {

    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String appPassword;
    private final boolean useTls;

    public AlarmEmailService(String smtpHost, int smtpPort, String username, String appPassword, boolean useTls) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.appPassword = appPassword;
        this.useTls = useTls;
    }

    public void sendEmail(String recipient, String subject, String body) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            if (useTls) props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", String.valueOf(smtpPort));

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("[EMAIL OK] Sent to " + recipient);

        } catch (Exception ex) {
            System.err.println("[EMAIL FAIL] " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}