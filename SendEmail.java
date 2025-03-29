package com.Practice.Hackathon.service;
import com.Practice.Hackathon.model.ClaimDetails;
import com.Practice.Hackathon.model.ClaimResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
@Service
public class SendEmail {
    @Autowired
    private ClaimDetailsServiceImpl claimDetailsService;

    public  void mail() {
        List<ClaimDetails> claimDetails = claimDetailsService.getAllClaimDetails();
        List<ClaimResponse> claimResponses = claimDetailsService.getAllClaimStatus();
        for (ClaimDetails claimDetails1 : claimDetails) {
            for (ClaimResponse claimResponse1 : claimResponses) {
                if (claimDetails1.getClaim_Id().equals(claimResponse1.getClaimId())) {
                    String to = claimDetails1.getEmail();

                    // Set up the SMTP server properties
                    String host = "smtp.gmail.com"; // SMTP server for Gmail
                    String port = "587"; // TLS port for Gmail SMTP
                    String from = "snpmouli@gmail.com"; // Sender's email
                    String password = "uvar fcqk medj jipt"; // Sender's email password

                    // Set up the properties for the session
                    Properties properties = new Properties();
                    properties.put("mail.smtp.host", host);
                    properties.put("mail.smtp.port", port);
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.starttls.enable", "true"); // TLS encryption

                    // Create a session with the email server
                    Session session = Session.getInstance(properties, new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(from, password);
                        }
                    });

                    try {
                        System.out.println("Before Email sent successfully!");
                        String claimStatus = claimResponse1.getClaimStatus();// Example claim status
                        Long claimId = claimDetails1.getClaim_Id(); // Example claim ID

                        // Build HTML content with a table
                        String htmlContent = "<html><body>"
                                + "<h2>Claim Status Report</h2>"
                                + "<table border='1' cellpadding='5'>"
                                + "<tr><th>Claim ID</th><th>Claim Status</th></tr>"
                                + "<tr><td>" + claimId + "</td><td>" + claimStatus + "</td></tr>"
                                + "</table>"
                                + "</body></html>";

                        // Create a MimeMessage object
                        Message message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(from));
                        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                        message.setSubject("Claim Status Report");

                        // Set the content as HTML
                        message.setContent(htmlContent, "text/html");
                        // Send the email
                        Transport.send(message);

                        System.out.println("Email sent successfully!");
                        String status=claimDetailsService.deleteClaimDetails(claimDetails1.getClaim_Id());
                        System.out.println(status);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }
}
