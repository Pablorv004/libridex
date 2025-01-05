package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.entity.User;
import com.main.libridex.service.LendingService;
import com.main.libridex.service.ReservationService;
import com.main.libridex.service.UserService;

@Controller
@RequestMapping("/reservations")
public class UserReservationsController {

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @Autowired
    @Qualifier("reservationService")
    private ReservationService reservationService;

    @Autowired
    @Qualifier("lendingService")
    private LendingService lendingService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    private static final String YOUR_RESERVATIONS = "your-reservations";
    @GetMapping("")
    public String getUserReservations(Model model) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        model.addAttribute("reservations", reservationService.findByUserId(user.getId()));
        model.addAttribute("lendings", lendingService.findByUserId(user.getId()));
        accessLogger.accessed("reservations");
        return YOUR_RESERVATIONS;
    }
}
