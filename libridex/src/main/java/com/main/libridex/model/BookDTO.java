package com.main.libridex.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    
    private Integer id;

    @NotBlank(message = "The title must not be empty")
    @Size(max = 30, message = "The title cannot be more than 20 characters long")
    private String title;

    private String image;

    @NotBlank(message = "The author must not be empty")
    @Size(max = 25, message = "The author cannot be more than 20 characters long")
    private String author;

    @NotBlank(message = "The genre must not be empty")
    @Size(max = 15, message = "The genre cannot be more than 15 characters long")
    private String genre;

    @NotNull(message = "You must select a publishing date")
    @PastOrPresent(message = "You cannot select a future date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishing_date;

    private LocalDateTime created_at;

    private boolean lent;

    private boolean reserved;
}
