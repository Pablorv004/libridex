package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Lending;

public interface LendingService {
    List<Lending> findByBookId(Integer id);
    boolean createLending(Integer bookId);
    void endLending(Integer id);
    List<Lending> getAllLendings();
    boolean existsInUserLendings(Integer bookId);
    Lending findBookCurrentLending(Integer id);
}
