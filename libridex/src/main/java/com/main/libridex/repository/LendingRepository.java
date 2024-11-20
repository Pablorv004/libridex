package com.main.libridex.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Lending;

@Repository("lendingRepository")
public interface LendingRepository extends JpaRepository<Lending,Serializable>{
}
