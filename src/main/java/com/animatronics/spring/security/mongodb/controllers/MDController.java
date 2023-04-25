package com.animatronics.spring.security.mongodb.controllers;

import java.util.Map;
// import java.time.LocalDateTime;
// import java.util.List;
import java.util.Optional;
import java.util.Set;

// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<MoneyDiscipline> updateMD(@PathVariable("id") String id,
            @RequestBody Map<String, Double> body) {
        Optional<MoneyDiscipline> mdData = mdRepository.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = ((UserDetails) authentication.getPrincipal()).getUsername();
        String mdOwner = mdRepository.findById(id).get().getOwner();

        if (mdData.isPresent()) {
            MoneyDiscipline _md = mdData.get();
            double deposit = body.get("deposit");
            double withdraw = body.get("withdraw");
            double tempBalance = mdData.get().getBalance() + deposit - withdraw;

            if (user.equals(mdOwner)) {
                if (tempBalance < 0) {
                    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    _md.setBalance(tempBalance);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(mdRepository.save(_md), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/money/{id}")
    public ResponseEntity<HttpStatus> deleteMD(@PathVariable("id") String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String user = ((UserDetails) authentication.getPrincipal()).getUsername();
            String mdOwner = mdRepository.findById(id).get().getOwner();

            Optional<User> userData = userRepository.findByUsername(user);
            Set<Role> roles = userData.get().getRoles();
            boolean isAdmin = false;

            for (Role role : roles) {
                if (role.getId().equals("6420f5471a943f643d51bcf6")) {
                    isAdmin = true;
                    break;
                }
            }

            if (user.equals(mdOwner) || isAdmin == true) {
                mdRepository.deleteById(id);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
