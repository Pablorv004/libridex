package com.main.libridex.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Book;

@Repository("bookRepository")
public interface BookRepository extends JpaRepository<Book,Serializable>{
    Book findById(Integer id);
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, Integer id);
    
    @Query("SELECT MAX(b.id) FROM Book b")
    Integer findMaxId();
}
