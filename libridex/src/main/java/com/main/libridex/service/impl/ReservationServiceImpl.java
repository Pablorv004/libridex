package com.main.libridex.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.main.libridex.entity.Book;
import com.main.libridex.entity.Reservation;
import com.main.libridex.entity.User;
import com.main.libridex.model.ReservationDTO;
import com.main.libridex.repository.BookRepository;
import com.main.libridex.repository.ReservationRepository;
import com.main.libridex.repository.UserRepository;
import com.main.libridex.service.ReservationService;

@Service("reservationService")
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    @Qualifier("reservationRepository")
    ReservationRepository reservationRepository;

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Autowired
    @Qualifier("bookRepository")
    BookRepository bookRepository;

    @Override
    public List<Reservation> findByBookId(Integer id) {
        return reservationRepository.findByBookId(id);
    }

    @Override
    public boolean createReservation(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Book book = bookRepository.findById(bookId);
        
        reservationRepository.save(new Reservation(user, book));

        return true;
    }

    @Override
    public boolean endReservation(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        for(Reservation reservation : user.getReservations()){
            if(bookId == reservation.getBook().getId() && reservation.getStatus().equals("Pending")){
                reservation.setStatus("Ended");
                reservationRepository.save(reservation);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<ReservationDTO> getAllReservationsDTO() {
        List<ReservationDTO> reservationList = new ArrayList<>();
        for (Reservation r : reservationRepository.findAll())
            reservationList.add(toDTO(r));

        return reservationList;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public boolean isAlreadyReserved(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        for(Reservation res : user.getReservations()){
            if(res.getBook().getId() == bookId && res.getStatus().equalsIgnoreCase("Pending"))
            return true;
        }

        return false;
    }

    // TODO: When calling this method, ensure that the null is controlled (If there's no reserves, it'll return null)
    @SuppressWarnings("null")
    @Override
    public Reservation findBookCurrentReservation(Integer bookId) {
        Reservation oldestReservation = null;

        for (Reservation reservation : reservationRepository.findByBookId(bookId))
            if (reservation.getReservation_date().isBefore(oldestReservation.getReservation_date()) && reservation.getStatus().equalsIgnoreCase("Pending"))
                oldestReservation = reservation;

        return oldestReservation;
    }

    // MODEL MAPPERS

    public Reservation toEntity(ReservationDTO dto) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(dto, Reservation.class);
    }

    public ReservationDTO toDTO(Reservation reservation) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(reservation, ReservationDTO.class);
    }
}
