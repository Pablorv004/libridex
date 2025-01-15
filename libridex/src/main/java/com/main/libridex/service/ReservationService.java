package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Reservation;
import com.main.libridex.entity.User;
import com.main.libridex.model.ReservationDTO;

public interface ReservationService {
    List<Reservation> findAll();
    List<Reservation> findByBookId(Integer id);
    boolean createReservation(Integer bookId);
    boolean endReservation(Integer id);
    boolean endReservationByForce(Integer id);
    List<ReservationDTO> getAllReservationsDTO();
    List<Reservation> getAllReservations();
    boolean isReserved(Integer bookId);
    boolean isReservedByUser(Integer bookId);
    boolean isUserTurn(Integer bookId);
    Reservation findBookCurrentReservation(Integer id);
    User findUserCurrentReservation(Integer id);
    List<Reservation> findByUserId(Integer userId);
    List<Reservation> findByUserIdAndStatusNotEnded(Integer userId);
}
