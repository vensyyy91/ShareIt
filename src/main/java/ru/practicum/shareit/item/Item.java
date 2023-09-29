package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "owner_id", nullable = false)
    private long owner;
    @Column(name = "available", nullable = false)
    private Boolean available;
    private long request;
}