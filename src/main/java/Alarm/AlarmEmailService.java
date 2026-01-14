package Alarm;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

/** provides functionality to send email notifications for alarm events
 * to notify clinicians or staff when vital signs exceed thresholds
 */
public class AlarmEmailService {
    // SMTP server configuration
    private final String smtpHost; //hostname
    private final int smtpPort; //port
    private final String username; //username email
    private final String appPassword; //token/apssword for application
    private final boolean useTls; //TLS encryption?

    //constructs AlarmEmailService with specified SMTP configuration
    public AlarmEmailService(String smtpHost, int smtpPort, String username, String appPassword, boolean useTls) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.username = username;
        this.appPassword = appPassword;
        this.useTls = useTls;
    }

    //send email to specified recipient with given subject and body.
    public void sendEmail(String recipient, String subject, String body) {
        try {
            // set SMTP properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");  // enable authentication
            if (useTls) props.put("mail.smtp.starttls.enable", "true"); // enable TLS if requested
            props.put("mail.smtp.host", smtpHost); // SMTP server hostname
            props.put("mail.smtp.port", String.valueOf(smtpPort)); // SMTP server port

            // create authenticated session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, appPassword);
                }
            });

            //email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            //send email
            Transport.send(message);
            System.out.println("[EMAIL OK] Sent to " + recipient);

        } catch (Exception ex) {
            System.err.println("[EMAIL FAIL] " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}