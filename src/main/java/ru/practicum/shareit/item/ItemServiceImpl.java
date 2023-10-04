package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemInfoDto> getAllItems(long userId) {
        getUser(userId);
        List<ItemInfoDto> items = itemRepository.findAllByOwnerOrderById(userId).stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList());
                    List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
                    return ItemMapper.toItemInfoDto(item, getLastBooking(bookings), getNextBooking(bookings), comments);
                })
                .collect(Collectors.toList());
        log.info(String.format("Возвращен список вещей для пользователя с id=%d: %s", userId, items));

        return items;
    }

    @Override
    public ItemInfoDto getItemById(long userId, long itemId) {
        getUser(userId);
        Item item = getItem(itemId);
        log.info("Возвращена вещь: " + item);
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
        BookingInfoDto lastBooking = null;
        BookingInfoDto nextBooking = null;
        if (item.getOwner() == userId) {
            lastBooking = getLastBooking(bookings);
            nextBooking = getNextBooking(bookings);
        }

        return ItemMapper.toItemInfoDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    @Transactional
    public ItemDto addItem(long userId, ItemDto itemDto) {
        getUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        Item newItem = itemRepository.save(item);
        log.info(String.format("Пользователем с id=%d добавлена вещь: %s", userId, newItem));

        return ItemMapper.toItemDto(newItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        getUser(userId);
        Item oldItem = getItem(itemId);
        long ownerId = oldItem.getOwner();
        if (userId != ownerId) {
            throw new AccessDeniedException("Редактировать вещь может только её владелец.");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(userId);
        if (item.getName() == null) {
            item.setName(oldItem.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(oldItem.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(oldItem.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);
        log.info("Владельцем обновлена вещь: " + updatedItem);

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> foundItems = itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info(String.format("Выполнен поиск по запросу: \"%s\". Найдены вещи: %s", text, foundItems));

        return foundItems;
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = getUser(userId);
        Item item = getItem(itemId);
        if (bookingRepository.findAllByItemId(itemId).stream()
                .noneMatch(booking -> booking.getStatus() == Status.APPROVED &&
                                booking.getEnd().isBefore(LocalDateTime.now()) &&
                                booking.getBooker().getId() == userId)) {
            throw new ItemUnavailableException(String.format("Пользователь с id=%d не брал в аренду вещь с id=%d", userId, itemId));
        }
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment);

        return CommentMapper.toCommentDto(newComment);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id= " + userId + " не найден."));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id=" + itemId + " не найдена."));
    }

    private BookingInfoDto getLastBooking(List<Booking> bookings) {
        Optional<Booking> booking = bookings.stream()
                .filter(b -> !b.getStart().isAfter(LocalDateTime.now()))
                .filter(b -> b.getStatus() == Status.APPROVED)
                .max(Comparator.comparing(Booking::getStart));

        return booking.map(BookingMapper::toBookingInfoDto).orElse(null);
    }

    private BookingInfoDto getNextBooking(List<Booking> bookings) {
        Optional<Booking> booking = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .filter(b -> b.getStatus() == Status.APPROVED)
                .min(Comparator.comparing(Booking::getStart));

        return booking.map(BookingMapper::toBookingInfoDto).orElse(null);
    }
}