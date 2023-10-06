package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(long userId);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllCurrentByBookerId(long userId, LocalDateTime date);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItemOwnerOrderByStartDesc(long userId);

    List<Booking> findAllByItemOwnerAndStartAfterOrderByStartDesc(long userId, LocalDateTime date);

    List<Booking> findAllByItemOwnerAndEndBeforeOrderByStartDesc(long userId, LocalDateTime date);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.owner = ?1 " +
            "and ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> findAllCurrentByItemOwner(long userId, LocalDateTime date);

    List<Booking> findAllByItemOwnerAndStatusOrderByStartDesc(long userId, Status status);

    List<Booking> findAllByItemId(long itemId);
}