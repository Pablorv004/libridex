package com.main.libridex.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.main.libridex.entity.Reservation;
import com.main.libridex.model.ReservationDTO;
import com.main.libridex.repository.ReservationRepository;
import com.main.libridex.service.ReservationService;

@Service("reservationService")
public class ReservationServiceImpl implements ReservationService{

    @Autowired
    @Qualifier("reservationRepository")
    ReservationRepository reservationRepository;

    @Override
    public void save(Reservation reservation) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public void delete(Integer id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationDTO> getAllReservations() {
        List<ReservationDTO> reservationList = new ArrayList<>();
        for(Reservation r : reservationRepository.findAll())
            reservationList.add(toDTO(r));

        return reservationList;
    }

    public Reservation toEntity(ReservationDTO dto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(dto, Reservation.class);
    }

    public ReservationDTO toDTO(Reservation reservation) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(reservation, ReservationDTO.class);
    }
}
