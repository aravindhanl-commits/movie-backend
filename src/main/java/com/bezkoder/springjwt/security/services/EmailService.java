package com.bezkoder.springjwt.security.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    private static final String RESEND_URL = "https://api.resend.com/emails";

    public void sendBookingConfirmation(String toEmail, String bookingId, String seatNumbers, double totalAmount) {

        String subject = "🎟️ Booking Confirmed - Booking ID: " + bookingId;

        String textBody = "Hello,\n\nYour booking has been successfully confirmed!\n\n"
                + "Booking ID: " + bookingId + "\n"
                + "Seats: " + seatNumbers + "\n"
                + "Total Amount: ₹" + totalAmount + "\n\n"
                + "Enjoy your movie!\n\n"
                + "Best Regards,\nMovie Booking Team";

        // JSON body for Resend API
        String json = String.format("""
        {
            "from": "%s",
            "to": ["%s"],
            "subject": "%s",
            "text": "%s"
        }
        """, "cineverse186@gmail.com", toEmail, subject, textBody.replace("\n", "\\n"));

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(resendApiKey);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        try {
            restTemplate.exchange(RESEND_URL, HttpMethod.POST, request, String.class);
            System.out.println("📨 Email sent to " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Email failed: " + e.getMessage());
        }
    }
}
