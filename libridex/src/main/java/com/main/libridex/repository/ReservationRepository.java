package com.main.libridex.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Reservation;

@Repository("reservationRepository")
public interface ReservationRepository extends JpaRepository<Reservation,Serializable>{
    Reservation findById(Integer id);
    List<Reservation> findByBookId(Integer id);
    List<Reservation> findByUserId(Integer id);
}
