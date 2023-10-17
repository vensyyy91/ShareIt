package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.*;
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
class ItemServiceIT {
    private final EntityManager em;
    private final ItemService itemService;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Comment comment1;
    private Comment comment2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
        item1 = new Item(null, "item1", "first item for test", 1L, true, 0L);
        item2 = new Item(null, "item2", "second item for test", 1L, true, 0L);
        booking1 = new Booking(
                null,
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now().minusMinutes(5),
                item1,
                user1,
                Status.APPROVED
        );
        booking2 = new Booking(
                null,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10),
                item1,
                user2,
                Status.APPROVED
        );
        comment1 = new Comment(null, "first comment for test", item1, user1, LocalDateTime.now());
        comment2 = new Comment(null, "second comment for test", item1, user2, LocalDateTime.now().plusMinutes(12));
    }

    @Test
    void getAllItems() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(comment1);
        em.persist(comment2);
        BookingInfoDto bookingDto1 = new BookingInfoDto(1L, 1L, booking1.getStart(), booking1.getEnd());
        BookingInfoDto bookingDto2 = new BookingInfoDto(2L, 2L, booking2.getStart(), booking2.getEnd());
        CommentDto commentDto1 = new CommentDto(1L, "first comment for test", "user1", comment1.getCreated());
        CommentDto commentDto2 = new CommentDto(2L, "second comment for test", "user2", comment2.getCreated());

        List<ItemInfoDto> items = itemService.getAllItems(1L, 0, 10);

        assertEquals(2, items.size());
        assertEquals(1L, items.get(0).getId());
        assertEquals("item1", items.get(0).getName());
        assertEquals("first item for test", items.get(0).getDescription());
        assertTrue(items.get(0).getAvailable());
        assertEquals(bookingDto1, items.get(0).getLastBooking());
        assertEquals(bookingDto2, items.get(0).getNextBooking());
        assertIterableEquals(List.of(commentDto1, commentDto2), items.get(0).getComments());
        assertEquals(2L, items.get(1).getId());
        assertEquals("item2", items.get(1).getName());
        assertEquals("second item for test", items.get(1).getDescription());
        assertTrue(items.get(1).getAvailable());
        assertNull(items.get(1).getLastBooking());
        assertNull(items.get(1).getNextBooking());
        assertTrue(items.get(1).getComments().isEmpty());
    }

    @Test
    void getItemById() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);
        em.persist(booking2);
        em.persist(comment1);
        em.persist(comment2);
        BookingInfoDto bookingDto1 = new BookingInfoDto(1L, 1L, booking1.getStart(), booking1.getEnd());
        BookingInfoDto bookingDto2 = new BookingInfoDto(2L, 2L, booking2.getStart(), booking2.getEnd());
        CommentDto commentDto1 = new CommentDto(1L, "first comment for test", "user1", comment1.getCreated());
        CommentDto commentDto2 = new CommentDto(2L, "second comment for test", "user2", comment2.getCreated());

        ItemInfoDto item = itemService.getItemById(1L, 1L);

        assertEquals(1L, item.getId());
        assertEquals("item1", item.getName());
        assertEquals("first item for test", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(bookingDto1, item.getLastBooking());
        assertEquals(bookingDto2, item.getNextBooking());
        assertIterableEquals(List.of(commentDto1, commentDto2), item.getComments());
    }

    @Test
    void addItem() {
        em.persist(user1);
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        itemService.addItem(1L, itemDto1);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto1.getName()).getSingleResult();

        assertEquals(1L, item.getId());
        assertEquals("item1", item.getName());
        assertEquals("first item for test", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void updateItem() {
        em.persist(user1);
        em.persist(item1);
        ItemDto updateItemDto = new ItemDto(null, "updatedItem", "updated description", null, null);
        itemService.updateItem(1L, 1L, updateItemDto);

        TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i WHERE i.id = :id", Item.class);
        Item item = query.setParameter("id", item1.getId()).getSingleResult();

        assertEquals(1L, item.getId());
        assertEquals("updatedItem", item.getName());
        assertEquals("updated description", item.getDescription());
        assertTrue(item.getAvailable());
    }

    @Test
    void searchItem() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        ItemDto itemDto1 = ItemMapper.toItemDto(item1);
        ItemDto itemDto2 = ItemMapper.toItemDto(item2);
        List<ItemDto> search1 = itemService.searchItem("item1", 0,10);
        List<ItemDto> search2 = itemService.searchItem("second", 0, 10);
        List<ItemDto> search3 = itemService.searchItem("item", 0, 10);

        assertEquals(1, search1.size());
        assertEquals(itemDto1, search1.get(0));
        assertEquals(1, search2.size());
        assertEquals(itemDto2, search2.get(0));
        assertEquals(2, search3.size());
        assertIterableEquals(List.of(itemDto1, itemDto2), search3);
    }

    @Test
    void addComment() {
        em.persist(user1);
        em.persist(item1);
        em.persist(booking1);
        CommentDto commentDto1 = new CommentDto(null, "test comment for item1 from user1", null, null);
        itemService.addComment(1L, 1L, commentDto1);

        TypedQuery<Comment> query = em.createQuery("SELECT c FROM Comment AS c WHERE c.text = :text", Comment.class);
        Comment comment = query.setParameter("text", commentDto1.getText()).getSingleResult();

        assertEquals(1L, comment.getId());
        assertEquals(commentDto1.getText(), comment.getText());
        assertEquals(item1, comment.getItem());
        assertEquals(user1, comment.getAuthor());
        assertNotNull(comment.getCreated());
    }
}