package com.main.libridex.service;

import java.util.List;

import com.main.libridex.entity.Reservation;
import com.main.libridex.model.ReservationDTO;

public interface ReservationService {
    void save(Reservation reservation);
    void delete(Integer id);
    List<ReservationDTO> getAllReservations();
}
