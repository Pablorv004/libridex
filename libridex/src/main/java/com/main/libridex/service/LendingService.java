package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Lending;

public interface LendingService {
    List<Lending> findByBookId(Integer id);
    boolean createLending(Integer bookId, String userEmail);
    void endLending(Integer id);
    List<Lending> getAllLendings();
    boolean existsInUserLendings(String email, Integer bookId);
    Lending findBookCurrentLending(Integer id);
}
