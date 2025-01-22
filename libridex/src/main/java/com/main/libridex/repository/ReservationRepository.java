package com.main.libridex.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Reservation;

@Repository("reservationRepository")
public interface ReservationRepository extends JpaRepository<Reservation,Serializable>{
    Reservation findById(Integer id);
    List<Reservation> findByBookId(Integer id);
    List<Reservation> findByUserId(Integer id);
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :id AND r.status != 'Ended'")
    List<Reservation> findByUserIdAndStatusNotEnded(Integer id);
    long count();
}
