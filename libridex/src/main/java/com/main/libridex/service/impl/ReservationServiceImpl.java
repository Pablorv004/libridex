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
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
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
    public boolean endReservationByForce(Integer id) {
        Reservation reservation = reservationRepository.findById(id);

        if(reservation != null){
            reservation.setStatus("Ended");
            reservationRepository.save(reservation);
            return true;
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
    public User findUserCurrentReservation(Integer bookId) {
        Reservation reservation = findBookCurrentReservation(bookId);
        return reservation != null ? reservation.getUser() : null;
    }

    @Override
    public boolean isReservedByUser(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);

        for(Reservation res : user.getReservations()){
            if(res.getBook().getId() == bookId && res.getStatus().equalsIgnoreCase("Pending"))
            return true;
        }

        return false;
    }

    @Override
    public long count() {
        return reservationRepository.count();
    }

    @Override
    public boolean isReserved(Integer bookId) {
        List<Reservation> reservations = reservationRepository.findByBookId(bookId);

        for(Reservation res : reservations)
            if(res.getStatus().equalsIgnoreCase("Pending"))
                return true;

        return false;
    }

    // This method searchs for the oldest reserve available of a specific book, and then returns it
    @Override
    public Reservation findBookCurrentReservation(Integer bookId) {
        Reservation oldestReservation = null;

        for (Reservation reservation : reservationRepository.findByBookId(bookId)) {
            if (reservation.getStatus().equalsIgnoreCase("Pending") && 
                (oldestReservation == null || reservation.getReservationDate().isBefore(oldestReservation.getReservationDate()))) {
                oldestReservation = reservation;
            }
        }

        return oldestReservation;
    }

    // This method determines if it's the user's turn to lend the book based on the oldest reservation date of the passed book and the user's reservation
    // date for that book
    @Override
    public boolean isUserTurn(Integer bookId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Reservation reservation = findBookCurrentReservation(bookId);

        if(reservation != null){
            for(Reservation userReservation : user.getReservations()){
                if(userReservation.getReservationDate().equals(reservation.getReservationDate()) && userReservation.getStatus().equalsIgnoreCase("Pending"))
                    return true;
            }
        }

        return false;
    }

    @Override
    public List<Reservation> findByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Override
    public List<Reservation> findByUserIdAndStatusNotEnded(Integer userId) {
        return reservationRepository.findByUserIdAndStatusNotEnded(userId);
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
