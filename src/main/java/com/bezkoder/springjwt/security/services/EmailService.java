package com.bezkoder.springjwt.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Send booking confirmation email
    public void sendBookingConfirmation(String toEmail, String bookingId, String seatNumbers, double totalAmount) {
        String subject = "🎟️ Booking Confirmed - Booking ID: " + bookingId;
        String message = "Hello,\n\nYour booking has been successfully confirmed!\n\n"
                + "Booking ID: " + bookingId + "\n"
                + "Seats: " + seatNumbers + "\n"
                + "Total Amount: ₹" + totalAmount + "\n\n"
                + "Enjoy your movie!\n\n"
                + "Best Regards,\nMovie Booking Team";

        sendSimpleMail(toEmail, subject, message);
    }

    // ✅ Send simple mail with logging and error handling
    private void sendSimpleMail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(System.getenv("SPRING_MAIL_USERNAME")); // Use env variable
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            logger.info("Attempting to send email to {}", to);
            mailSender.send(message);
            logger.info("Email successfully sent to {}", to);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
        }
    }
}
