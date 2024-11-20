package com.main.libridex.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.main.libridex.entity.Reservation;

@Repository("reservationRepository")
public interface ReservationRepository extends JpaRepository<Reservation,Serializable>{
}
