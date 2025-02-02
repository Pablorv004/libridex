package com.main.libridex.service;

import java.util.List;
import java.util.Map;

import com.main.libridex.entity.Book;
import com.main.libridex.entity.Lending;
import com.main.libridex.entity.User;

public interface LendingService {
    List<Lending> findAll();
    List<Lending> findByBookId(Integer id);
    boolean createLending(Integer bookId);
    void endLending(Integer id);
    List<Lending> getAllLendings();
    User findUserCurrentLending(Integer id);
    int countUserActiveLendings(User user, int bookId);
    boolean isLendByUser(Integer bookId);
    Lending findBookCurrentLending(Integer id);
    List<Lending> findByUserId(Integer userId);
    List<Lending> findByUserIdAndEndDateIsNull(Integer userId);
    Map<User, Integer> filterLendingsPerUser(String searchString);
    Map<Book, Integer> filterLendingsPerBook(String searchString);
    Map<String, Integer> countLendingsPerMonth();
    long count();
}
