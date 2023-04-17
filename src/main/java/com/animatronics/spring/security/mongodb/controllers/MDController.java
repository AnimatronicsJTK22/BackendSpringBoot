package com.animatronics.spring.security.mongodb.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animatronics.spring.security.mongodb.models.MoneyDiscipline;
import com.animatronics.spring.security.mongodb.models.User;
import com.animatronics.spring.security.mongodb.repository.MDRepository;
import com.animatronics.spring.security.mongodb.repository.UserRepository;
import com.animatronics.spring.security.mongodb.models.Role;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class MDController {

    @Autowired
    MDRepository mdRepository;

    @Autowired
    UserRepository userRepository;

    @PutMapping("/money/{id}")
    public ResponseEntity<MoneyDiscipline> updateMD(@PathVariable("id") String id, @RequestBody MoneyDiscipline md,
            @RequestBody double deposit, @RequestBody double withdraw) {
        Optional<MoneyDiscipline> mdData = mdRepository.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = ((UserDetails) authentication.getPrincipal()).getUsername();
        String mdOwner = mdRepository.findById(id).get().getOwner();
        withdraw = 0;
        deposit = 0;
        boolean condition = false;

        if (mdData.isPresent()) {
            MoneyDiscipline _md = mdData.get();

            if (md.getBalance() < withdraw) {
                condition = false;
            } else {
                condition = true;
            }

            if (condition == true) {
                _md.setBalance(md.getBalance() + deposit - withdraw);
                if (user.equals(mdOwner)) {
                    return new ResponseEntity<>(mdRepository.save(_md), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.FORBIDDEN);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
