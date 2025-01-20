package com.main.libridex.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;

import com.main.libridex.entity.User;
import com.main.libridex.model.SecureUserDTO;
import com.main.libridex.model.UserDTO;

public interface UserService {
    List<User> findAll();
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    List<SecureUserDTO> findAllSecureUsers();
    Page<SecureUserDTO> findFirstN(int elementsNumber);
    boolean existsByEmail(String email);
    boolean existsByEmailExceptCurrent(String email, Integer id);
    User save(User user);
    public User findById(Integer id);
    public void deleteById(Integer id);
    void edit(UserDTO userDTO);
    boolean isEditValid(UserDTO userDTO, BindingResult bindingResult);
    void toggleActivation(Integer id);
    User toEntity(UserDTO userDTO);
    User toEntity(SecureUserDTO secureUserDTO);
    UserDTO toDTO(User user);
    SecureUserDTO toSecureDTO(User user);
    User register(User user);
    boolean isRegisterValid(UserDTO userDTO, BindingResult bindingResult);
    long countByRoleNot(String role);
}
