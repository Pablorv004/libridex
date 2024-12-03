package com.main.libridex.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.main.libridex.entity.User;
import com.main.libridex.model.UserDTO;
import com.main.libridex.repository.UserRepository;
import com.main.libridex.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        UserBuilder builder = null;

        if(user!=null){
            builder = org.springframework.security.core.userdetails.User.withUsername(email);
            builder.password(user.getPassword());
            builder.authorities(new SimpleGrantedAuthority(user.getRole()));
        } else {
            throw new UsernameNotFoundException("User not found");
        }

        return builder.build();
    }

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
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailExceptCurrent(String email, Integer id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    public User register(User user) {
        user.setSurname("");
        user.setRole("ROLE_USER");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void edit(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElse(null);

        if (user != null) {
            user.setName(userDTO.getName());
            user.setSurname(userDTO.getSurname());
            user.setEmail(userDTO.getEmail());
            userRepository.save(user);
        }
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

    public boolean isEditValid(UserDTO userDTO, BindingResult bindingResult) {
        //Name Requirements
        if (userDTO.getName().isEmpty() || userDTO.getName() == null)
            bindingResult.rejectValue("name", "error.user", "Name is required.");

        //Surname Requirements
        if (!userDTO.getSurname().matches("([A-Za-z]+\\s)*[A-Za-z]*"))
            bindingResult.rejectValue("surname", "error.surname", "Surname is invalid.");

        //Email Requirements
        if (userDTO.getEmail().isEmpty() || userDTO.getEmail() == null)
            bindingResult.rejectValue("email", "error.user", "Email is required.");
        else if (existsByEmailExceptCurrent(userDTO.getEmail(), userDTO.getId()))
            bindingResult.rejectValue("email", "error.user", "An account with this email already exists.");
        else if (!isEmailValid(userDTO.getEmail()))
            bindingResult.rejectValue("email", "error.user", "Invalid email format.");

        return !bindingResult.hasErrors();
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
}
