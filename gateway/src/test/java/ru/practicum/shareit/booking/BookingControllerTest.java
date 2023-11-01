package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingClient bookingClient;
    private static final String BOOKINGS_PATH = "/bookings";

    @Test
    @SneakyThrows
    void addBooking_withUserIdAndValidDto_shouldReturnOk() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("X-Sharer-User-Id", "1");
        ResponseEntity<Object> response = new ResponseEntity<>(booking, headers, HttpStatus.OK);
        when(bookingClient.addBooking(1, booking)).thenReturn(response);

        String result = mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), result);
    }

    @Test
    @SneakyThrows
    void addBooking_withoutUserId_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withoutItemId_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                null,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withoutStart_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                null,
                LocalDateTime.now().plusMinutes(10)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withoutEnd_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                null
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withStartInPast_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().minusMinutes(5),
                LocalDateTime.now().plusMinutes(10)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withEndInPast_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().minusMinutes(5)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withStartEqualEnd_shouldReturnBadRequest() {
        LocalDateTime time = LocalDateTime.now().plusMinutes(5);
        BookingDto booking = new BookingDto(1L, time, time);

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void addBooking_withStartAfterEnd_shouldReturnBadRequest() {
        BookingDto booking = new BookingDto(
                1L,
                LocalDateTime.now().plusMinutes(10),
                LocalDateTime.now().plusMinutes(5)
        );

        mvc.perform(post(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    @SneakyThrows
    void approveBooking_withUserIdAndParam_shouldReturnOk() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approveBooking(1, 1, true);
    }

    @Test
    @SneakyThrows
    void approveBooking_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void approveBooking_withoutApprovedParam_shouldReturnBadRequest() {
        mvc.perform(patch(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    @SneakyThrows
    void getBooking_withUserId_shouldReturnOk() {
        mvc.perform(get(BOOKINGS_PATH + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getBooking(1, 1);
    }

    @Test
    @SneakyThrows
    void getBooking_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH + "/1")).andExpect(status().isBadRequest());

        verify(bookingClient, never()).getBooking(anyLong(), anyLong());
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

        verify(bookingClient).getUserBookings(1, BookingState.CURRENT, 1, 5);
    }

    @Test
    @SneakyThrows
    void getUserBookings_withUserIdAndWithoutParams_shouldReturnOk() {
        mvc.perform(get(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getUserBookings(1, BookingState.ALL, 0, 10);
    }

    @Test
    @SneakyThrows
    void getUserBookings_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH)).andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserBookings_withInvalidFromParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "ALL");
        parameters.add("from", "-1");
        parameters.add("size", "5");

        mvc.perform(get(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserBookings_withInvalidSizeParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "ALL");
        parameters.add("from", "0");
        parameters.add("size", "0");

        mvc.perform(get(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserBookings_withInvalidStateParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "QWERTY");
        parameters.add("from", "0");
        parameters.add("size", "10");

        mvc.perform(get(BOOKINGS_PATH)
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
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

        verify(bookingClient).getUserItemsBookings(1, BookingState.CURRENT, 1, 5);
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withUserIdAndWithoutParams_shouldReturnOk() {
        mvc.perform(get(BOOKINGS_PATH + "/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        verify(bookingClient).getUserItemsBookings(1, BookingState.ALL, 0, 10);
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withoutUserId_shouldReturnBadRequest() {
        mvc.perform(get(BOOKINGS_PATH + "/owner")).andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserItemsBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withInvalidFromParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "ALL");
        parameters.add("from", "-1");
        parameters.add("size", "5");

        mvc.perform(get(BOOKINGS_PATH + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserItemsBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withInvalidSizeParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "ALL");
        parameters.add("from", "0");
        parameters.add("size", "0");

        mvc.perform(get(BOOKINGS_PATH + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserItemsBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }

    @Test
    @SneakyThrows
    void getUserItemsBookings_withInvalidStateParam_shouldReturnBadRequest() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("state", "QWERTY");
        parameters.add("from", "0");
        parameters.add("size", "10");

        mvc.perform(get(BOOKINGS_PATH + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .params(parameters))
                .andExpect(status().isBadRequest());

        verify(bookingClient, never()).getUserItemsBookings(anyLong(), any(BookingState.class), anyInt(), anyInt());
    }
}