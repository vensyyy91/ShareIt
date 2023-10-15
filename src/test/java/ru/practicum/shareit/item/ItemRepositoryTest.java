package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
        item1 = new Item(null, "item1", "first test item", 1L, true, null);
        item2 = new Item(null, "item2", "second test item", 2L, true, 1L);
        item3 = new Item(null, "item3", "third test item", 1L, false, 1L);
    }

    @Test
    void search_shouldReturnOnlyAvailableAndWithMatchingNameOrDescription() {
        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(item2);
        em.persist(item3);

        List<Item> items = itemRepository.search("d tESt", PageRequest.of(0, 5)).getContent();

        assertEquals(1, items.size());
        assertEquals(2L, items.get(0).getId());
        assertEquals("item2", items.get(0).getName());
        assertEquals("second test item", items.get(0).getDescription());
        assertEquals(2L, items.get(0).getOwner());
        assertTrue(items.get(0).getAvailable());
        assertEquals(1L, items.get(0).getRequest());
    }
}