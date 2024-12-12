package com.main.libridex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.main.libridex.components.logger.AccessLogger;
import com.main.libridex.components.logger.UserLogger;
import com.main.libridex.entity.User;
import com.main.libridex.model.UserDTO;
import com.main.libridex.service.UserService;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final String PROFILE_VIEW = "profile";
    private static final String PROFILE_EDIT_VIEW = "profile_edit";

    @Autowired
    @Qualifier("accessLogger")
    private AccessLogger accessLogger;

    @Autowired
    @Qualifier("userLogger")
    private UserLogger userLogger;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @GetMapping("")
    public String getProfile(Model model, User user) {
        model.addAttribute("user", userService.findByEmail(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
        accessLogger.accessed("profile");
        return PROFILE_VIEW;
    }

    @GetMapping("/edit")
    public String editProfile(Model model, User user) {
        model.addAttribute("user", userService.findByEmail(((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()));
        accessLogger.accessed("profile/edit");
        return PROFILE_EDIT_VIEW;
    }
    
    @PostMapping("/apply")
    public String applyChanges(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult bindingResult, RedirectAttributes flash){

        if(!userService.isEditValid(userDTO, bindingResult)){
            userLogger.failedToUpdateProfile(userDTO.getEmail(), new Exception("Invalid data. Either data doesn't match regex or email is already in use."));
            return PROFILE_EDIT_VIEW;
        } else {
            userService.edit(userDTO);
            userLogger.updatedProfile(userDTO.getEmail());
            flash.addFlashAttribute("success", "Profile edited successfully!");
            return "redirect:/profile";
        }
    }
}
