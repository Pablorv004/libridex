package com.main.libridex.repository;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Book;

@Repository("bookRepository")
public interface BookRepository extends JpaRepository<Book, Serializable> {
    Book findById(Integer id);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Integer id);

    @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    Page<Book> findFirstNSortedByCreatedAt(PageRequest pageRequest);

    @Query("SELECT MAX(b.id) FROM Book b")
    Integer findMaxId();

    @Query("SELECT b FROM Book b WHERE (:genres IS NULL OR b.genre IN :genres) AND (:authors IS NULL OR b.author IN :authors) AND (:startDate IS NULL OR b.publishingDate >= :startDate)")
    Page<Book> findAllWithFilters(PageRequest pageRequest, List<String> genres, List<String> authors, LocalDate startDate);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.author) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Book> searchByTitleOrAuthor(String query, PageRequest pageRequest);
}
