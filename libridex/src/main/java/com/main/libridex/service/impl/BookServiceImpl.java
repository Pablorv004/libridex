package com.main.libridex.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public Page<Book> findPaginatedWithFilters(int pageNumber, List<String> genres, List<String> authors, String sortBy) {
        Sort sort = switch (sortBy) {
            case "title_asc" -> Sort.by("title").ascending();
            case "author_asc" -> Sort.by("author").ascending();
            case "genre_asc" -> Sort.by("genre").ascending();
            case "publishingDate_desc" -> Sort.by("publishingDate").descending();
            case "upload_date_desc" -> Sort.by("createdAt").descending();
            case "reservations_desc" -> Sort.by("reservations.size").descending();
            default -> Sort.by("title").ascending();
        };
        return bookRepository.findAllWithFilters(PageRequest.of(pageNumber, 6, sort), genres, authors);
    }

    /**
     * Method to order a map by values in reverse order
     */
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap) {
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        unsortedMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
        return sortedMap;
    }

    @Override
    public Map<String, Integer> findGenresWithAmountByBook() {
        Map<String, Integer> genresWithAmount = new HashMap<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            String genre = book.getGenre();
            genresWithAmount.put(genre, 1 + genresWithAmount.getOrDefault(genre, 0));
        }
        return sortByValue(genresWithAmount);
    }

    @Override
    public Map<String, Integer> findAuthorsWithAmountByBook() {
        Map<String, Integer> authorsWithAmount = new HashMap<>();
        List<Book> books = bookRepository.findAll();
        for (Book book : books) {
            String author = book.getAuthor();
            authorsWithAmount.put(author, 1 + authorsWithAmount.getOrDefault(author, 0));
        }
        return sortByValue(authorsWithAmount);
    }

    @Override
    public BookDTO findById(Integer id) {
        return toDTO(bookRepository.findById(id));
    }

    @Override
    public Book save(BookDTO bookDTO) {
        if (bookDTO.getId() == null) {
            bookDTO.setCreatedAt(LocalDateTime.now());
        }
        return bookRepository.save(toEntity(bookDTO));
    }

    @Override
    public void deleteById(Integer id) {
        Book book = bookRepository.findById(id);
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
            if (!bookDTO.getImage().isBlank()) {
                Book book = bookRepository.findById(id);
                CloudinaryUtils.deleteImage(book.getImage());
            }

            bookDTO.setImage(CloudinaryUtils.uploadImage(imageFile, id, "Book"));
        } else {
            // If no image is selected, set the default image
            if (bookDTO.getImage().isBlank()) {
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

    @Override
    public Page<Book> findFirstNSortedByCreatedAt(int elementsNumber) {
        return bookRepository.findFirstNSortedByCreatedAt(PageRequest.of(0, elementsNumber));
    }
}
