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
import com.animatronics.spring.security.mongodb.models.Role;
import com.animatronics.spring.security.mongodb.repository.DiaryRepository;
import com.animatronics.spring.security.mongodb.repository.UserRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DiaryController {

  @Autowired
  DiaryRepository diaryRepository;

  @Autowired
  UserRepository userRepository;

  // Ambil data diary sesuai dengan owner
  @GetMapping("/diaries")
  public ResponseEntity<List<Diary>> getAllDiariesByUserLoggedIn() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String owner = ((UserDetails) authentication.getPrincipal()).getUsername();

      Optional<User> userData = userRepository.findByUsername(owner);
      if (userData.isPresent()) {
        Set<Role> roles = userData.get().getRoles();
        boolean isAdmin = false;
        for (Role role : roles) {
          if (role.getId().equals("6420f5471a943f643d51bcf6")) {
            isAdmin = true;
            break;
          }
        }

        List<Diary> diaries;
        if (isAdmin) {
          // Retrieve all diaries
          diaries = diaryRepository.findAll();
        } else {
          // Retrieve diaries owned by the authenticated user
          diaries = diaryRepository.findByOwner(owner);
        }

        if (diaries.isEmpty()) {
          return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(diaries, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/diaries/{id}")
  public ResponseEntity<Diary> getDiaryById(@PathVariable("id") String id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String owner = ((UserDetails) authentication.getPrincipal()).getUsername();

    Optional<Diary> diaryData = diaryRepository.findById(id);

    if (diaryData.isPresent()) {
      if (diaryData.get().getOwner().equals(owner)) {
        return new ResponseEntity<>(diaryData.get(), HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/diaries")
  public ResponseEntity<Diary> createDiary(@RequestBody Diary diary) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String owner = authentication.getName();

      Diary _diary = diaryRepository
          .save(new Diary(diary.getTitle(), diary.getContent(), false, owner, LocalDateTime.now()));

      return new ResponseEntity<>(_diary, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/diaries/{id}")
  public ResponseEntity<Diary> updateDiary(@PathVariable("id") String id, @RequestBody Diary diary) {
    Optional<Diary> diaryData = diaryRepository.findById(id);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String user = ((UserDetails) authentication.getPrincipal()).getUsername();
    String diaryOwner = diaryRepository.findById(id).get().getOwner();

    if (diaryData.isPresent()) {
      Diary _diary = diaryData.get();
      _diary.setTitle(diary.getTitle());
      _diary.setContent(diary.getContent());
      _diary.setVisibility(diary.isVisibility());
      if (user.equals(diaryOwner)) {
        return new ResponseEntity<>(diaryRepository.save(_diary), HttpStatus.OK);
      }else{
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/diaries/{id}")
  public ResponseEntity<HttpStatus> deleteDiary(@PathVariable("id") String id) {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String user = ((UserDetails) authentication.getPrincipal()).getUsername();
      String diaryOwner = diaryRepository.findById(id).get().getOwner();

      Optional<User> userData = userRepository.findByUsername(user);
      Set<Role> roles = userData.get().getRoles();
      boolean isAdmin = false;

      for (Role role : roles) {
        if (role.getId().equals("6420f5471a943f643d51bcf6")) {
          isAdmin = true;
          break;
        }
      }

      if (user.equals(diaryOwner) || isAdmin == true) {
        diaryRepository.deleteById(id);
      }

      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/diaries")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  public ResponseEntity<HttpStatus> deleteAllDiaries() {
    try {
      diaryRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/diaries/public")
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
  public ResponseEntity<List<Diary>> findByVisible() {
    try {
      List<Diary> diarys = diaryRepository.findByVisibility(true);

      if (diarys.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(diarys, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}