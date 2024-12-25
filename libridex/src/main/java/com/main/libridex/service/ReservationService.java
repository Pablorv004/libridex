package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Reservation;
import com.main.libridex.model.ReservationDTO;

public interface ReservationService {
    List<Reservation> findByBookId(Integer id);
    boolean createReservation(Integer bookId);
    boolean endReservation(Integer id);
    List<ReservationDTO> getAllReservationsDTO();
    List<Reservation> getAllReservations();
    boolean isReserved(Integer bookId);
    boolean isReservedByUser(Integer bookId);
    boolean isUserTurn(Integer bookId);
    Reservation findBookCurrentReservation(Integer id);
}
