package com.animatronics.spring.security.mongodb.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animatronics.spring.security.mongodb.models.Diary;
import com.animatronics.spring.security.mongodb.models.History;
import com.animatronics.spring.security.mongodb.models.User;
import com.animatronics.spring.security.mongodb.models.Role;
import com.animatronics.spring.security.mongodb.repository.HistoryRepository;
import com.animatronics.spring.security.mongodb.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class HistoryController {

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/history/{id}")
    public ResponseEntity<History> getHistoryById(@PathVariable("id") String id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String owner = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<History> historyData = historyRepository.findById(id);

        if (historyData.isPresent()){
            if (historyData.get().getId().equals(owner)) {
                return new ResponseEntity<>(historyData.get(), HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/history")
    public ResponseEntity<HttpStatus> deleteAllHistory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String user = ((UserDetails) authentication.getPrincipal()).getUsername();
            
            Optional<User> userData = userRepository.findByUsername(user);
            Set<Role> roles = userData.get().getRoles();
            boolean isAdmin = false;
            for (Role role : roles) {
                if (role.getId().equals("6420f5471a943f643d51bcf6")) {
                  isAdmin = true;
                  break;
                }
            }
            if (isAdmin == true) {
                historyRepository.deleteAll();
              }
        
            return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<History>> getAllHistory() {
        try {
            List<History> historyList = new ArrayList<>();
            historyRepository.findAll().forEach(historyList::add);

            if (historyList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(historyList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/history")
    public ResponseEntity<History> createHistory(@RequestBody History history) {
        try {
            History _history = historyRepository.save(new History(history.getDesc(), history.getDesc(), history.getBalanceDesc(), LocalDateTime.now()));

            return new ResponseEntity<>(_history, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
