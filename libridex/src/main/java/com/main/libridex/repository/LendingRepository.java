package com.main.libridex.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Lending;

@Repository("lendingRepository")
public interface LendingRepository extends JpaRepository<Lending, Serializable> {
    Lending findById(Integer id);
    List<Lending> findByBookId(Integer id);
    List<Lending> findByUserId(Integer id);
    List<Lending> findByUserIdAndEndDateIsNull(Integer id);
    @Query("SELECT l.user.id, COUNT(l) FROM Lending l GROUP BY l.user.id")
    List<Object[]> countLendingsGroupedByUserId();
}
