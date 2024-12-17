package com.main.libridex.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String title;

    private String image;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private LocalDate publishing_date;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private boolean lent;

    @Column(nullable = false)
    private boolean reserved;

    @OneToMany(mappedBy = "book")
    public List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    public List<Lending> lendings = new ArrayList<>();
}