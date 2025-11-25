package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.repository.BookingRepository;
import com.bezkoder.springjwt.repository.SeatRepository;
import com.bezkoder.springjwt.security.services.BookingService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SeatRepository seatRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private BookingService bookingService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testGenerateBookingId_uniqueAndLength() {
        Booking b1 = new Booking();
        b1.setSeatNumbers("A1");
        b1.setShowId(1L);
        b1.setUserId(1L);
        b1.setTotalAmount(100.0);

        Booking b2 = new Booking();
        b2.setSeatNumbers("B1");
        b2.setShowId(1L);
        b2.setUserId(2L);
        b2.setTotalAmount(50.0);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking saved1 = bookingService.createBooking(b1);
        Booking saved2 = bookingService.createBooking(b2);

        assertNotNull(saved1.getBookingId());
        assertNotNull(saved2.getBookingId());
        assertEquals(12, saved1.getBookingId().length());
        assertEquals(12, saved2.getBookingId().length());
        assertNotEquals(saved1.getBookingId(), saved2.getBookingId());
    }

    @Test
    void testCreateBooking_marksSeatsBooked_andSendsMessages() {
        Booking booking = new Booking();
        booking.setSeatNumbers("A1,A2");
        booking.setShowId(10L);
        booking.setUserId(5L);
        booking.setTotalAmount(200.0);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(123L);
            b.setBookingId("TESTBOOKING01");
            b.setBookingTime(LocalDateTime.now());
            return b;
        });

        Seat seatMock = new Seat();
        seatMock.setId(1L);
        seatMock.setSeatNumber("A1");

        when(seatRepository.findByShowIdAndSeatNumber(eq(10L), anyString()))
                .thenReturn(Optional.of(seatMock));

        // THE FIX
        doNothing().when(messagingTemplate)
                .<Object>convertAndSend(any(String.class), ArgumentMatchers.<Object>any());

        Booking saved = bookingService.createBooking(booking);

        verify(bookingRepository, atLeastOnce()).save(any(Booking.class));
        verify(seatRepository, times(2)).findByShowIdAndSeatNumber(eq(10L), anyString());

        verify(messagingTemplate, times(2))
                .<Object>convertAndSend(contains("/topic/show/10"), ArgumentMatchers.<Object>any());
    }
}
