package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    private static final String BOOKINGS_PATH = "/bookings";

    @Test
    @SneakyThrows
    void addBooking_withUserIdAndValidDto_shouldReturnOk() {
        BookingCreationDto booking = new BookingCreationDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );
        BookingDto bookingSaved = new BookingDto(
                1L,
                booking.getStart(),
                booking.getEnd(),
                new BookingDto.Item(1L, "item1"),
                new BookingDto.Booker(1L, "user1"),
                Status.WAITING
        );
        when(bookingService.addBooking(1, booking)).thenReturn(bookingSaved);

        String result = mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingSaved), result);
    }

    @Test
    @SneakyThrows
    void addBooking_withoutUserId_shouldReturnBadRequest() {
        BookingCreationDto booking = new BookingCreationDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).addBooking(anyLong(), any(BookingCreationDto.class));
    }

    @Test
    @SneakyThrows
    void approveBooking_withUserIdAndParam_shouldReturnOk() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingService).approveBooking(1, 1, true);
    }

    @Test
    @SneakyThrows
    void approveBooking_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void approveBooking_withoutApprovedParam_shouldReturnBadRequest() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getBooking_withUserId_shouldReturnOk() {
        mvc.perform(get(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingService).getBooking(1, 1);
    }

    @Test
    @SneakyThrows
    void getBooking_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(bookingService, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getUserBookings_withUserIdAndWithParams_shouldReturnOk() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "CURRENT");
        parameters.add("from", "1");
        parameters.add("size", "5");

        mvc.perform(get(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isOk());

        verify(bookingService).getUserBookings(1, State.CURRENT, 1, 5);
    }

    @Test
    @SneakyThrows
    void getUserBookings_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH)).andExpect(status().isBadRequest());

        verify(bookingService, never()).getUserBookings(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withUserIdAndWithParams_shouldReturnOk() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "CURRENT");
        parameters.add("from", "1");
        parameters.add("size", "5");

        mvc.perform(get(BOOKINGS_PATH + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isOk());

        verify(bookingService).getUserItemsBookings(1, State.CURRENT, 1, 5);
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH + "/owner")).andExpect(status().isBadRequest());

        verify(bookingService, never()).getUserItemsBookings(anyLong(), any(State.class), anyInt(), anyInt());
    }
}