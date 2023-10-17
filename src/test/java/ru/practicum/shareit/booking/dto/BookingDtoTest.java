package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    @SneakyThrows
    void testBookingDto_withAllFields() {
        BookingDto.Item item = new BookingDto.Item(1L, "item1");
        BookingDto.Booker booker = new BookingDto.Booker(1L, "user1");
        BookingDto bookingDto = new BookingDto(
                1L,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                item,
                booker,
                Status.WAITING
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.start").isNotNull();
        assertThat(result).extractingJsonPathValue("$.end").isNotNull();
        assertThat(result).extractingJsonPathValue("$.item").hasFieldOrProperty("id");
        assertThat(result).extractingJsonPathValue("$.item").hasFieldOrProperty("name");
        assertThat(result).extractingJsonPathValue("$.booker").hasFieldOrProperty("id");
        assertThat(result).extractingJsonPathValue("$.booker").hasFieldOrProperty("name");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo("WAITING");
    }
}