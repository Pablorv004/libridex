package com.main.libridex.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.main.libridex.entity.Reservation;
import com.main.libridex.model.BookDTO;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.repository.ReservationRepository;
import com.main.libridex.service.BookService;
import com.main.libridex.utils.CloudinaryUtils;

@Service("bookService")
public class BookServiceImpl implements BookService {

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Autowired
    @Qualifier("reservationRepository")
    ReservationRepository reservationRepository;

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
    public Page<Book> findPaginatedWithFilters(int pageNumber, List<String> genres, List<String> authors,
            String sortBy, String publishingDateRange) {
        Sort sort = switch (sortBy) {
            case "title_asc" -> Sort.by("title").ascending();
            case "author_asc" -> Sort.by("author").ascending();
            case "genre_asc" -> Sort.by("genre").ascending();
            case "publishingDate_desc" -> Sort.by("publishingDate").descending();
            case "upload_date_desc" -> Sort.by("createdAt").descending();
            default -> Sort.by("title").ascending();
        };
        LocalDate now = LocalDate.now();
        LocalDate startDate = switch (publishingDateRange) {
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            case "year" -> now.minusYears(1);
            default -> null;
        };
        return bookRepository.findAllWithFilters(PageRequest.of(pageNumber, 6, sort), genres, authors, startDate);
    }

    @Override
    public Page<Book> searchBooks(String query, int pageNumber) {
        return bookRepository.searchByTitleOrAuthor(query, PageRequest.of(pageNumber, 6));
    }

    /**
     * Retrieves the first N most reserved books.
     * 
     * This method calls the findMostReservedBooks method to get a list of the most
     * reserved books and then selects the first N books from that list.
     * 
     * @param elementsNumber the number of most reserved books to retrieve
     * @return a list of the first N most reserved books
     */
    @Override
    public List<Book> findFirstNMostReserved(int elementsNumber) {
        List<Book> mostReserved = new ArrayList<>();

        for (Book book : findMostReservedBooks()) {
            if (mostReserved.size() < 6)
                mostReserved.add(book);
        }

        return mostReserved;
    }

    /**
     * Finds the most reserved books.
     * 
     * This method retrieves all reservations from the reservation repository,
     * counts the number of reservations for each book, sorts the books by the
     * number of reservations in descending order, and then retrieves the book
     * entities from the book repository based on the sorted reservation counts.
     * 
     * @return a list of the most reserved books
     */
    @Override
    public List<Book> findMostReservedBooks() {
        List<Book> mostReservedBooks = new ArrayList<>();
        Map<Integer, Integer> bookReservationCount = new HashMap<>();

        // First we get all the reserves of all the books and put them in a hashmap
        for (Reservation reservation : reservationRepository.findAll()) {
            Integer bookId = reservation.getBook().getId();
            bookReservationCount.put(bookId, bookReservationCount.getOrDefault(bookId, 0) + 1);
        }

        // Secondly we sort the map by descendent order
        Map<Integer, Integer> sortedBookReservationCount = bookReservationCount.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);

        // Then we add all the content of the map into the arraylist
        for (Integer bookId : sortedBookReservationCount.keySet()) {
            mostReservedBooks.add(bookRepository.findById(bookId));
        }

        return mostReservedBooks;
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
    public long count() {
        return bookRepository.count();
    }

    @Override
    public long countDistinctAuthors() {
        return bookRepository.countDistinctAuthors();
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
                bookDTO.setImage("https://res.cloudinary.com/dlmbw4who/image/upload/v1736968304/default_image_vvjykw.png");
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
