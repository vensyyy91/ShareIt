package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdOrderByStartDesc(long userId, Pageable page);

    Page<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    Page<Booking> findAllCurrentByBookerId(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status, Pageable page);

    Page<Booking> findAllByItemOwnerOrderByStartDesc(long userId, Pageable page);

    Page<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date, Pageable page);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    Page<Booking> findAllCurrentByItemOwner(long userId, LocalDateTime date, Pageable page);

    Page<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(long userId, Status status, Pageable page);

    List<Booking> findAllByItemId(long itemId);
}