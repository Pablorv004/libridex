package com.main.libridex.service;

import com.main.libridex.entity.Reservation;

public interface ReservationService {
    void save(Reservation reservation);
    void delete(Integer id);
    void getAllReservations();
}
