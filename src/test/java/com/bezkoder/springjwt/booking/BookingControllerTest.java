package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.SpringBootSecurityJwtApplication;
import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.models.BookingRequest;
import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.repository.BookingRepository;
import com.bezkoder.springjwt.security.services.BookingService;
import com.bezkoder.springjwt.security.services.EmailService;
import com.bezkoder.springjwt.security.services.SeatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private SeatService seatService;

    @MockBean
    private EmailService emailService;

    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        sampleBooking = new Booking();
        sampleBooking.setId(5L);
        sampleBooking.setBookingId("CONFIRM001");
        sampleBooking.setShowId(7L);
        sampleBooking.setUserId(12L);
        sampleBooking.setSeatNumbers("B1,B2");
        sampleBooking.setTotalAmount(200.0);
        sampleBooking.setUserEmail("user2@example.com");
        sampleBooking.setPaymentStatus("PENDING");
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateBookingEndpoint() throws Exception {
        BookingRequest req = new BookingRequest();
        req.setUserId(1L);
        req.setMovieId(2L);
        req.setTheaterId(3L);
        req.setShowId(4L);
        req.setSeatNumbers("A1,A2");
        req.setTotalAmount(300.0);
        req.setUserEmail("user@example.com");

        Booking returned = new Booking();
        returned.setId(10L);
        returned.setBookingId("BOOK12345678");
        returned.setUserId(1L);
        returned.setShowId(4L);
        returned.setSeatNumbers("A1,A2");
        returned.setTotalAmount(300.0);
        returned.setBookingTime(LocalDateTime.now());
        returned.setPaymentStatus("PENDING");
        returned.setUserEmail("user@example.com");

        when(bookingService.createBooking(any(Booking.class))).thenReturn(returned);

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value("BOOK12345678"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testConfirmPayment_endpoint_marksSeatsAndSendsEmail() throws Exception {
        when(bookingRepository.findById(5L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        // Mock seatService.bookSeat() to return Optional<Seat>
        Seat seatMock = new Seat();
        seatMock.setId(1L);
        seatMock.setSeatNumber("B1");

        when(seatService.bookSeat(eq(7L), anyString(), eq(12L)))
                .thenReturn(Optional.of(seatMock));

        doNothing().when(emailService)
                .sendBookingConfirmation(anyString(), anyString(), anyString(), anyDouble());

        mockMvc.perform(put("/api/bookings/5/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        verify(seatService, times(2))
                .bookSeat(eq(7L), anyString(), eq(12L));

        verify(emailService, times(1))
                .sendBookingConfirmation(anyString(), anyString(), anyString(), anyDouble());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllBookings_endpoint_returnsList() throws Exception {
        Booking b1 = new Booking(); b1.setId(1L); b1.setBookingId("B1");
        Booking b2 = new Booking(); b2.setId(2L); b2.setBookingId("B2");

        when(bookingRepository.findAll()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingId").value("B1"))
                .andExpect(jsonPath("$[1].bookingId").value("B2"));
    }
}
