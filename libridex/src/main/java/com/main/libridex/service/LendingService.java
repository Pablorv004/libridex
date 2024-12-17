package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Lending;

public interface LendingService {
    void save(Integer bookId, String userEmail);
    void delete(Integer id);
    List<Lending> getAllLendings();
}
