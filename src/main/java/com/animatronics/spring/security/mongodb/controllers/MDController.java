package com.animatronics.spring.security.mongodb.controllers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.web.bind.annotation.GetMapping;
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
            @RequestBody Map<String, Object> request) {
        Optional<MoneyDiscipline> mdData = mdRepository.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = ((UserDetails) authentication.getPrincipal()).getUsername();

        if (mdData.isPresent()) {
            MoneyDiscipline _md = mdData.get();
            double balance = Double.parseDouble(request.get("balance").toString());
            String historyDesc = (String) request.get("historyDesc");
            String balanceChange = (String) request.get("balanceChange");
            String mdOwner = _md.getOwner();
            LocalDateTime time = LocalDateTime.now();

            if (user.equals(mdOwner)) {
                String[] existingLcd = _md.getLastChangeDesc();
                String[] newLcd = Arrays.copyOf(existingLcd, existingLcd.length + 1);
                newLcd[newLcd.length - 1] = historyDesc;
                String[] existingBC = _md.getBalanceDesc();
                String[] newBC = Arrays.copyOf(existingBC, existingBC.length + 1);
                newBC[newBC.length - 1] = balanceChange;
                LocalDateTime[] existingTime = _md.getTime();
                LocalDateTime[] newTime = Arrays.copyOf(existingTime, existingTime.length + 1);
                newTime[newTime.length - 1] = time;
                _md.setBalance(balance);
                _md.setLastChangeDesc(newLcd);
                _md.setBalanceDesc(newBC);
                _md.setTime(newTime);
            } else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            return new ResponseEntity<>(mdRepository.save(_md), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/money/{id}")
    public ResponseEntity<MoneyDiscipline> getMDById(@PathVariable("id") String id) {
        Optional<MoneyDiscipline> mdData = mdRepository.findById(id);

        if (mdData.isPresent()) {
            MoneyDiscipline moneyDiscipline = mdData.get();
            return new ResponseEntity<>(moneyDiscipline, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/money")
    public ResponseEntity<List<MoneyDiscipline>> getAllMD() {
        List<MoneyDiscipline> mdList = mdRepository.findAll();

        if (mdList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(mdList, HttpStatus.OK);
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
