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

import com.main.libridex.entity.Book;
import com.main.libridex.model.BookDTO;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.service.BookService;

@Service("bookService")
public class BookServiceImpl implements BookService {

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @Override
    public Page<Book> findAll(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest);
    }

    @Override
    public BookDTO findById(Integer id) {
        return toDTO(bookRepository.findById(id).orElse(null));
    }

    @Override
    public Book save(BookDTO bookDTO) {
        // TODO: IMPLEMENT EDITING FUNCTIONALITY (MUST UPDATE RESERVATIONS AND LENDINGS)
        return bookRepository.save(toEntity(bookDTO));
    }

    @Override
    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }

    @Override
    public String saveImage(MultipartFile imageFile) {
        String uploadDir = "src/main/resources/static/images/";
        String imageName = imageFile.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        try (InputStream inputStream = imageFile.getInputStream()) {
            Path filePath = uploadPath.resolve(imageName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }

    // VALIDATIONS

    public void checkExistentBook(BookDTO bookDTO, BindingResult bResult) {
        if (bookRepository.existsByTitle(bookDTO.getTitle()))
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
