package com.main.libridex.service.impl;

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
import com.main.libridex.utils.CloudinaryUtils;

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
    public Page<Book> findFirstN(int elementsNumber) {
        return bookRepository.findAll(PageRequest.of(0, elementsNumber));
    }

    @Override
    public Page<Book> findPaginated(int pageNumber) {
        return bookRepository.findAll(PageRequest.of(pageNumber, 6));
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
        CloudinaryUtils.deleteImage(book.getImage());
        bookRepository.deleteById(id);
    }

    private Integer getLastId(BookDTO bookDTO) {
        Integer id = bookDTO.getId();
        if (id != null) 
            return id;
        Integer newId = bookRepository.findMaxId();
        if (newId != null)
            return ++newId;
        return 1;
    }

    @Override
    public void setImage(BookDTO bookDTO, MultipartFile imageFile) {
        Integer id = getLastId(bookDTO);

        // Upload the image to Cloudinary if there is one selected
        if (!imageFile.isEmpty()) {
            // Delete the previous image if it exists
            if(!bookDTO.getImage().isBlank()){
                Book book = bookRepository.findById(id).orElse(null);
                CloudinaryUtils.deleteImage(book.getImage());
            }

            bookDTO.setImage(CloudinaryUtils.uploadImage(imageFile, id, "Book"));
        } else {
            // If no image is selected, set the default image
            if(bookDTO.getImage().isBlank()) {
                bookDTO.setImage("https://res.cloudinary.com/dlmbw4who/image/upload/v1734182372/default_image.png");
            }
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
