package com.main.libridex.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;

import com.main.libridex.entity.User;
import com.main.libridex.model.UserDTO;
import com.main.libridex.model.secureUserDTO;

public interface UserService {
    List<User> findAll();
    Page<secureUserDTO> findAll(PageRequest pageRequest);
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
    boolean existsByEmailExceptCurrent(String email, Integer id);
    User save(User user);
    public User findById(Integer id);
    public void deleteById(Integer id);
    void edit(UserDTO userDTO);
    boolean isEditValid(UserDTO userDTO, BindingResult bindingResult);
}
