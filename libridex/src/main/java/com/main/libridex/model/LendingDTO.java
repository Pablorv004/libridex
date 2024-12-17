package com.main.libridex.model;

import java.time.LocalDate;

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
public class LendingDTO {
    private Integer id;

    private User user;
    
    private Book book;

    private LocalDate start_date;

    private LocalDate end_date;
}
