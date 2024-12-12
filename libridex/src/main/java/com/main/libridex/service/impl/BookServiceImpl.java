package com.main.libridex.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.main.libridex.controller.FileController;
import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.service.BookService;
import com.main.libridex.service.StorageService;

@Service("bookService")
public class BookServiceImpl implements BookService {

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Autowired
    @Qualifier("storageService")
    StorageService storageService;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> findAll(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest);
    }

    @Override
    public Page<Book> findFirstN(int elementsNumber) {
        return bookRepository.findAll(PageRequest.of(0, elementsNumber));
    }

    @Override
    public BookDTO findById(Integer id) {
        return toDTO(bookRepository.findById(id).orElse(null));
    }

    @Override
    public Book save(BookDTO bookDTO) {
        return bookRepository.save(toEntity(bookDTO));
    }

    @Override
    public void deleteById(Integer id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book.getImage() != null && !book.getImage().isBlank()) {
            storageService.delete(book.getImage());
        }
        bookRepository.deleteById(id);
    }

    @Override
    public void setImage(BookDTO bookDTO, MultipartFile imageFile) {
        Integer id = bookDTO.getId();

        if (id == null)
            id = bookRepository.findMaxId() + 1;

        if (!imageFile.isEmpty()) {
            if (!bookDTO.getImage().isBlank())
                storageService.delete(bookDTO.getImage());

            String image = storageService.store(imageFile, id, "Book");
            bookDTO.setImage(image);
        }
    }

    // VALIDATIONS

    public void checkExistentBook(BookDTO bookDTO, BindingResult bResult) {
        if (bookRepository.existsByTitleAndIdNot(bookDTO.getTitle(), bookDTO.getId()))
            bResult.rejectValue("title", "error.title", "There already exists a book with that title");
    }

    // MODEL MAPPER

    public BookDTO toDTO(Book book) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(book, BookDTO.class);
    }

    public Book toEntity(BookDTO bookDTO) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(bookDTO, Book.class);
    }
}
