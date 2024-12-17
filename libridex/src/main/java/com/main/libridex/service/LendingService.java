package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Lending;

public interface LendingService {
    Lending findByBookId(Integer id);
    boolean createLending(Integer bookId, String userEmail);
    void deleteLending(Integer id);
    List<Lending> getAllLendings();
    boolean existsInUserLendings(String email, Integer bookId);
}
