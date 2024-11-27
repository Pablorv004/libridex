package com.main.libridex.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.User;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User,Serializable>{
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    boolean existsByEmail(String email);
}
