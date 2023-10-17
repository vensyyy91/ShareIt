package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerId(long userId, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfter(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBefore(long userId, LocalDateTime date, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end")
    Page<Booking> findAllCurrentByBookerId(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByBookerIdAndStatus(long userId, Status status, Pageable page);

    Page<Booking> findAllByItemOwner(long userId, Pageable page);

    Page<Booking> findAllByItemOwnerAndStartAfter(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByItemOwnerAndEndBefore(long userId, LocalDateTime date, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end")
    Page<Booking> findAllCurrentByItemOwner(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByItemOwnerAndStatus(long userId, Status status, Pageable page);

    List<Booking> findAllByItemId(long itemId);
}