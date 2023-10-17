package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIT {
    private final EntityManager em;
    private final UserService userService;
    private User user1;
    private User user2;

    @BeforeEach
    void beforeEach() {
        user1 = new User(null, "user1", "user1@test.com");
        user2 = new User(null, "user2", "user2@test.com");
    }

    @Test
    void getAllUsers() {
        em.persist(user1);
        em.persist(user2);

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals("user1", users.get(0).getName());
        assertEquals("user1@test.com", users.get(0).getEmail());
        assertEquals(2L, users.get(1).getId());
        assertEquals("user2", users.get(1).getName());
        assertEquals("user2@test.com", users.get(1).getEmail());
    }

    @Test
    void getUserById() {
        em.persist(user1);

        UserDto user = userService.getUserById(1L);

        assertEquals(1L, user.getId());
        assertEquals("user1", user.getName());
        assertEquals("user1@test.com", user.getEmail());
    }

    @Test
    void createUser() {
        UserDto userDto1 = UserMapper.toUserDto(user1);
        userService.createUser(userDto1);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("user1", user.getName());
        assertEquals("user1@test.com", user.getEmail());
    }

    @Test
    void updateUser() {
        em.persist(user1);
        UserDto userDto = new UserDto(null, "updatedUser", "update@test.com");
        userService.updateUser(1L, userDto);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.id = :id", User.class);
        User user = query.setParameter("id", 1L).getSingleResult();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("updatedUser", user.getName());
        assertEquals("update@test.com", user.getEmail());
    }

    @Test
    void deleteUser() {
        em.persist(user1);
        userService.deleteUser(1L);

        TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u WHERE u.email = :email", User.class);
        List<User> user = query.setParameter("email", user1.getEmail()).getResultList();

        assertTrue(user.isEmpty());
    }
}