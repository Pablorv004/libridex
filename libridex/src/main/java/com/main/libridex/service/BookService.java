package com.main.libridex.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;

public interface BookService {
    List<Book> findAll();

    Page<Book> findFirstN(int elementsNumber);

    Page<Book> findFirstNSortedByCreatedAt(int elementsNumber);

    List<Book> findFirstNMostReserved(int elementsNumber);

    List<Book> findMostReservedBooks();

    BookDTO findById(Integer id);

    Book save(BookDTO bookDTO);

    void deleteById(Integer id);

    Map<String, Integer> findGenresWithAmountByBook();

    Map<String, Integer> findAuthorsWithAmountByBook();

    void checkExistentBook(BookDTO bookDTO, BindingResult bResult);

    void setImage(BookDTO bookDTO, MultipartFile imageFile);

    Page<Book> findPaginated(int pageNumber);

    Page<Book> findPaginatedWithFilters(int pageNumber, List<String> genres, List<String> authors, String sortBy, String publishingDateRange);

    Page<Book> searchBooks(String query, int pageNumber);
}
