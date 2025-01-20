package com.main.libridex.model;

import java.time.LocalDateTime;

import com.main.libridex.entity.Book;
import com.main.libridex.entity.User;

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
public class ReservationDTO {
    private Integer id;

    private LocalDateTime reservationDate;

    User user;

    Book book;
}
