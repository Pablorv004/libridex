package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.User;

public interface UserService {
    List<User> findAll();
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    boolean existsByEmailExceptCurrent(String email, Integer id);
    User save(User user);
}
