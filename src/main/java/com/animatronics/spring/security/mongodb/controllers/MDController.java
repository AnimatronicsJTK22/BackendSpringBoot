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
import org.springframework.data.mongodb.core.mapping.Document;
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
import com.animatronics.spring.security.mongodb.models.User;
import com.animatronics.spring.security.mongodb.repository.MDRepository;
import com.animatronics.spring.security.mongodb.repository.UserRepository;
import com.animatronics.spring.security.mongodb.models.Role;

@RestController
@RequestMapping("/api")
public class MDController {

    @Autowired
    MDRepository mdRepository;

    @Autowired
    UserRepository userRepository;

    

}
