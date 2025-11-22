package com.bezkoder.springjwt.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Updated to include totalAmount and bookingId
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


    private void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("cineverse186@gmail.com"); // ✅ must match spring.mail.username
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
