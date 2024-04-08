package org.example;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void main(String[] args) {
        final String username = "gsx2002@yandex.ru";
        final String password = "zuwqekvjnzgdmseg";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.yandex.ru");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("gsx2002@yandex.ru"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("cool.gosha2002@gmail.com"));
            message.setSubject("Testing Email");
            message.setText("This is a test email sent from Java using SMTP");

            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
