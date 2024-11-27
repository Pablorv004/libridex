package com.main.libridex.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.main.libridex.entity.User;
import com.main.libridex.model.UserDTO;
import com.main.libridex.repository.UserRepository;
import com.main.libridex.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean passwordsMatch(String password, String password2) {
        return password.equals(password2);
    }

    public boolean isEmailValid(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    }

    public boolean isPasswordValid(String password) {
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        return hasUppercase && hasLowercase && hasNumber && password.length() >= 8;
    }

    public User register(User user) {
        user.setSurname("");
        user.setRole("ROLE_USER");
        return userRepository.save(user);
    }

    public boolean isRegisterValid(UserDTO userDTO, BindingResult bindingResult) {
        //Name Requirement
        if (userDTO.getName().isEmpty() || userDTO.getName() == null)
            bindingResult.rejectValue("name", "error.user", "Name is required.");
        //Email Requirements
        if (userDTO.getEmail().isEmpty() || userDTO.getEmail() == null)
            bindingResult.rejectValue("email", "error.user", "Email is required.");
        else if (existsByEmail(userDTO.getEmail()))
            bindingResult.rejectValue("email", "error.user", "An account with this email already exists.");
        else if (!isEmailValid(userDTO.getEmail()))
            bindingResult.rejectValue("email", "error.user", "Invalid email format.");
        //Password Requirements
        if (userDTO.getPassword().isEmpty() || userDTO.getPassword() == null)
            bindingResult.rejectValue("password", "error.user", "Password is required.");
        else if (!isPasswordValid(userDTO.getPassword()))
            bindingResult.rejectValue("password", "error.user",
                    "Password must be at least 8 characters long and contain uppercase, lowercase, and a number.");
        else if (!passwordsMatch(userDTO.getPassword(), userDTO.getRePassword()))
            bindingResult.rejectValue("rePassword", "error.user", "Passwords do not match.");
        return !bindingResult.hasErrors();
    }

}
