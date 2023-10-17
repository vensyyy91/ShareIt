package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
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
class ItemRequestServiceIT {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
        item1 = new Item(null, "item1", "first item for test", 1L, true, 1L);
        item2 = new Item(null, "item2", "second item for test", 1L, true, 1L);
        itemRequest1 = new ItemRequest(null, "first test request", user2, LocalDateTime.now().minusMinutes(5));
        itemRequest2 = new ItemRequest(null, "second test request", user2, LocalDateTime.now());
    }

    @Test
    void addRequest() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        ItemRequestDto itemRequestDto = new ItemRequestDto(
                null,
                "first test request",
                null,
                null,
                null
        );
        itemRequestService.addRequest(2L, itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery(
                "SELECT ir FROM ItemRequest AS ir WHERE ir.description = :description",
                ItemRequest.class
        );
        ItemRequest itemRequest = query.setParameter("description", itemRequestDto.getDescription()).getSingleResult();

        assertNotNull(itemRequest);
        assertEquals(1L, itemRequest.getId());
        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
        assertEquals(user2, itemRequest.getRequester());
        assertNotNull(itemRequest.getCreated());
    }

    @Test
    void getUserRequests() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(itemRequest1);
        em.persist(itemRequest2);

        List<ItemRequestDto> itemRequests = itemRequestService.getUserRequests(2L);

        assertEquals(2, itemRequests.size());
        assertEquals(2L, itemRequests.get(0).getId());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(2L, itemRequests.get(0).getRequester());
        assertEquals(itemRequest2.getCreated(), itemRequests.get(0).getCreated());
        assertTrue(itemRequests.get(0).getItems().isEmpty());
        assertEquals(1L, itemRequests.get(1).getId());
        assertEquals(itemRequest1.getDescription(), itemRequests.get(1).getDescription());
        assertEquals(2L, itemRequests.get(1).getRequester());
        assertEquals(itemRequest1.getCreated(), itemRequests.get(1).getCreated());
        assertEquals(2, itemRequests.get(1).getItems().size());
        assertEquals(1L, itemRequests.get(1).getItems().get(0).getId());
        assertEquals(2L, itemRequests.get(1).getItems().get(1).getId());
    }

    @Test
    void getAllRequests() {
        ItemRequest itemRequest3 =  new ItemRequest(
                null,
                "third test request",
                user1,
                LocalDateTime.now().minusMinutes(1)
        );
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(itemRequest1);
        em.persist(itemRequest2);
        em.persist(itemRequest3);

        List<ItemRequestDto> itemRequests = itemRequestService.getAllRequests(1L, 0, 10);

        assertEquals(2, itemRequests.size());
        assertEquals(2L, itemRequests.get(0).getId());
        assertEquals(itemRequest2.getDescription(), itemRequests.get(0).getDescription());
        assertEquals(2L, itemRequests.get(0).getRequester());
        assertEquals(itemRequest2.getCreated(), itemRequests.get(0).getCreated());
        assertTrue(itemRequests.get(0).getItems().isEmpty());
        assertEquals(1L, itemRequests.get(1).getId());
        assertEquals(itemRequest1.getDescription(), itemRequests.get(1).getDescription());
        assertEquals(2L, itemRequests.get(1).getRequester());
        assertEquals(itemRequest1.getCreated(), itemRequests.get(1).getCreated());
        assertEquals(2, itemRequests.get(1).getItems().size());
        assertEquals(1L, itemRequests.get(1).getItems().get(0).getId());
        assertEquals(2L, itemRequests.get(1).getItems().get(1).getId());
    }

    @Test
    void getRequestById() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(itemRequest1);

        ItemRequestDto itemRequest = itemRequestService.getRequestById(1L, 1L);

        assertNotNull(itemRequest);
        assertEquals(1L, itemRequest.getId());
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription());
        assertEquals(2L, itemRequest.getRequester());
        assertEquals(itemRequest1.getCreated(), itemRequest.getCreated());
    }
}