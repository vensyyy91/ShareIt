package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIT {
    private final EntityManager em;
    private final BookingService bookingService;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
        item1 = new Item(null, "item1", "first item for test", 1L, true, 0L);
        item2 = new Item(null, "item2", "second item for test", 1L, true, 0L);
        booking1 = new Booking(
                null,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10),
                item1,
                user2,
                Status.WAITING
        );
        booking2 = new Booking(
                null,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                item2,
                user2,
                Status.APPROVED
        );
    }

    @Test
    void addBooking() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        BookingCreationDto bookingDto1 = new BookingCreationDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );
        bookingService.addBooking(2L, bookingDto1);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(bookingDto1.getStart(), booking.getStart());
        assertEquals(bookingDto1.getEnd(), booking.getEnd());
        assertEquals(item1, booking.getItem());
        assertEquals(user2, booking.getBooker());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void approveBooking() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);
        bookingService.approveBooking(1L, 1L, true);

        TypedQuery<Booking> query = em.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", 1L).getSingleResult();

        assertNotNull(booking);
        assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void getBooking() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);

        BookingDto booking = bookingService.getBooking(1L, 1L);

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(booking1.getStart(), booking.getStart());
        assertEquals(booking1.getEnd(), booking.getEnd());
        assertEquals(1L, booking.getItem().getId());
        assertEquals("item1", booking.getItem().getName());
        assertEquals(2L, booking.getBooker().getId());
        assertEquals("user2", booking.getBooker().getName());
        assertEquals(Status.WAITING, booking.getStatus());
    }

    @Test
    void getUserBookings() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);

        List<BookingDto> bookings = bookingService.getUserBookings(2L, State.ALL, 0, 10);

        assertEquals(2, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals("item1", bookings.get(0).getItem().getName());
        assertEquals(2L, bookings.get(0).getBooker().getId());
        assertEquals("user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        assertEquals(2L, bookings.get(1).getId());
        assertEquals(booking2.getStart(), bookings.get(1).getStart());
        assertEquals(booking2.getEnd(), bookings.get(1).getEnd());
        assertEquals(2L, bookings.get(1).getItem().getId());
        assertEquals("item2", bookings.get(1).getItem().getName());
        assertEquals(2L, bookings.get(1).getBooker().getId());
        assertEquals("user2", bookings.get(1).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }

    @Test
    void getUserItemsBookings() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        User user3 = new User(null, "user3", "user3@test.com");
        Item item3 = new Item(null, "item3", "third item for test", 3L, true, 0L);
        Booking booking3 = new Booking(
                null,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(15),
                item3,
                user2,
                Status.WAITING
        );
        em.persist(user3);
        em.persist(item3);
        em.persist(booking3);

        List<BookingDto> bookings = bookingService.getUserItemsBookings(1L, State.ALL, 0, 10);

        assertEquals(2, bookings.size());
        assertEquals(1L, bookings.get(0).getId());
        assertEquals(booking1.getStart(), bookings.get(0).getStart());
        assertEquals(booking1.getEnd(), bookings.get(0).getEnd());
        assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals("item1", bookings.get(0).getItem().getName());
        assertEquals(2L, bookings.get(0).getBooker().getId());
        assertEquals("user2", bookings.get(0).getBooker().getName());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
        assertEquals(2L, bookings.get(1).getId());
        assertEquals(booking2.getStart(), bookings.get(1).getStart());
        assertEquals(booking2.getEnd(), bookings.get(1).getEnd());
        assertEquals(2L, bookings.get(1).getItem().getId());
        assertEquals("item2", bookings.get(1).getItem().getName());
        assertEquals(2L, bookings.get(1).getBooker().getId());
        assertEquals("user2", bookings.get(1).getBooker().getName());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }
}