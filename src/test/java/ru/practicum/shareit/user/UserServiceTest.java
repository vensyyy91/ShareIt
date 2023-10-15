package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1L, "user1", "user1@test.com");
        user2 = new User(2L, "user2", "user2@test.com");
        userDto1 = UserMapper.toUserDto(user1);
        userDto2 = UserMapper.toUserDto(user2);
    }

    @Test
    public void getAllUsers_whenHaveUsers_shouldReturnListWithUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> expected = List.of(userDto1, userDto2);
        List<UserDto> users = userService.getAllUsers();

        assertIterableEquals(expected, users);
    }

    @Test
    public void getAllUsers_whenEmpty_shouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> users = userService.getAllUsers();

        assertEquals(0, users.size());
    }

    @Test
    public void getUserById_withExistingId_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));

        UserDto user = userService.getUserById(1L);

        assertEquals(userDto1, user);
    }

    @Test
    public void getUsersById_withNotExistingId_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class, () -> userService.getUserById(99));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }

    @Test
    public void createUser_shouldReturnUser() {
        when(userRepository.save(user1)).thenReturn(user1);

        UserDto user = userService.createUser(userDto1);

        assertEquals(user, userDto1);
        verify(userRepository).save(user1);
    }

    @Test
    public void updateUser_withNameAndEmail_shouldReturnUserWithUpdatedNameAndEmail() {
        UserDto userDtoForUpdate = new UserDto(0L, "updatedUser", "updated@test.com");
        User updatedUser = UserMapper.toUser(userDtoForUpdate);
        updatedUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUser(1L, userDtoForUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(1L, savedUser.getId());
        assertEquals("updatedUser", savedUser.getName());
        assertEquals("updated@test.com", savedUser.getEmail());
    }

    @Test
    public void updateUser_onlyWithName_shouldReturnUserWithUpdatedNameAndOldEmail() {
        UserDto userDtoForUpdate = new UserDto(0L, "updatedUser", null);
        User updatedUser = UserMapper.toUser(userDtoForUpdate);
        updatedUser.setId(1L);
        updatedUser.setEmail("user1@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUser(1L, userDtoForUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(1L, savedUser.getId());
        assertEquals("updatedUser", savedUser.getName());
        assertEquals("user1@test.com", savedUser.getEmail());
    }

    @Test
    public void updateUser_onlyWithEmail_shouldReturnUserWithOldNameAndUpdatedEmail() {
        UserDto userDtoForUpdate = new UserDto(0L, null, "updated@test.com");
        User updatedUser = UserMapper.toUser(userDtoForUpdate);
        updatedUser.setId(1L);
        updatedUser.setName("user1");
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        userService.updateUser(1L, userDtoForUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(1L, savedUser.getId());
        assertEquals("user1", savedUser.getName());
        assertEquals("updated@test.com", savedUser.getEmail());
    }

    @Test
    public void deleteUser_withExistingId_shouldInvokeMethod() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user1));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUser_withNotExistingId_shouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Throwable ex = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99));
        assertEquals("Пользователь с id=99 не найден.", ex.getMessage());
    }
}