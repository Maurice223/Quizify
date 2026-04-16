package com.quizify.quizify.services;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String username) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            // Contenu HTML de l'email
            String htmlContent = 
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e0e0e0; border-radius: 10px; padding: 20px;'>" +
                    "<div style='text-align: center; background-color: #2c3e50; padding: 10px; border-radius: 8px 8px 0 0;'>" +
                        "<h1 style='color: #ffffff; margin: 0;'>Quizify</h1>" +
                    "</div>" +
                    "<div style='padding: 20px; text-align: center;'>" +
                        "<h2>Bienvenue, " + username + " ! 👋</h2>" +
                        "<p style='color: #555; font-size: 16px;'>Ton compte a été créé avec succès. Prépare-toi à relever de nouveaux défis techniques !</p>" +
                        "<p style='font-size: 12px; color: #999;'>Ceci est un email automatique, merci de ne pas y répondre.</p>" +
                    "</div>" +
                    "<div style='text-align: center; border-top: 1px solid #eee; padding-top: 20px; font-size: 12px; color: #7f8c8d;'>" +
                        "Développé par L'équipe Quizify" +
                    "</div>" +
                "</div>";

            helper.setText(htmlContent, true); // Le "true" ici est CRUCIAL pour activer le HTML
            helper.setTo(toEmail);
            helper.setSubject("Bienvenue sur Quizify ! 🚀");
            helper.setFrom("noreply@quizify.com");

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("Erreur email HTML : " + e.getMessage());
        }
    }
}