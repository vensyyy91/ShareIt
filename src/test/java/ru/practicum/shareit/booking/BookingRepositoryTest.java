package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Item item3;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
        user3 = new User(null, "user3", "user3@test.com");
        item1 = new Item(null, "item1", "first test item", 1L, true, null);
        item2 = new Item(null, "item2", "second test item", 2L, true, 1L);
        item3 = new Item(null, "item3", "third test item", 1L, false, 1L);
        booking1 = new Booking(
                null,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10),
                item1,
                user3,
                Status.WAITING
        );
        booking2 = new Booking(
                null,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                item2,
                user3,
                Status.APPROVED
        );
        booking3 = new Booking(
                null,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(5),
                item1,
                user2,
                Status.APPROVED
        );
        booking4 = new Booking(
                null,
                LocalDateTime.now().minusMinutes(3),
                LocalDateTime.now().plusMinutes(7),
                item3,
                user3,
                Status.APPROVED
        );
    }

    @Test
    void findAllCurrentByBookerId() {
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        em.persist(booking4);

        List<Booking> bookings = bookingRepository.findAllCurrentByBookerId(
                3L,
                LocalDateTime.now(),
                PageRequest.of(0, 5)
        ).getContent();

        assertEquals(2, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
        assertEquals(booking4.getStart(), bookings.get(0).getStart());
        assertEquals(booking4.getEnd(), bookings.get(0).getEnd());
        assertEquals(item3, bookings.get(0).getItem());
        assertEquals(user3, bookings.get(0).getBooker());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertEquals(2L, bookings.get(1).getId());
        assertEquals(booking2.getStart(), bookings.get(1).getStart());
        assertEquals(booking2.getEnd(), bookings.get(1).getEnd());
        assertEquals(item2, bookings.get(1).getItem());
        assertEquals(user3, bookings.get(1).getBooker());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }

    @Test
    void findAllCurrentByItemOwner() {
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(booking3);
        em.persist(booking4);

        List<Booking> bookings = bookingRepository.findAllCurrentByItemOwner(
                1L,
                LocalDateTime.now(),
                PageRequest.of(0, 5)
        ).getContent();

        assertEquals(2, bookings.size());
        assertEquals(4L, bookings.get(0).getId());
        assertEquals(booking4.getStart(), bookings.get(0).getStart());
        assertEquals(booking4.getEnd(), bookings.get(0).getEnd());
        assertEquals(item3, bookings.get(0).getItem());
        assertEquals(user3, bookings.get(0).getBooker());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
        assertEquals(3L, bookings.get(1).getId());
        assertEquals(booking3.getStart(), bookings.get(1).getStart());
        assertEquals(booking3.getEnd(), bookings.get(1).getEnd());
        assertEquals(item1, bookings.get(1).getItem());
        assertEquals(user2, bookings.get(1).getBooker());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }
}