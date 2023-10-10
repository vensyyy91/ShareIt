package ru.practicum.shareit.request;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "requester_id", nullable = false)
    private Long requester;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest itemRequest = (ItemRequest) o;
        return getId() != null && Objects.equals(getId(), itemRequest.getId());
    }

    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }
}