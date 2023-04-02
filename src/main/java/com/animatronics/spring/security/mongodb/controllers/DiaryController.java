package com.animatronics.spring.security.mongodb.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animatronics.spring.security.mongodb.models.Diary;
import com.animatronics.spring.security.mongodb.repository.DiaryRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class DiaryController {

  @Autowired
  DiaryRepository diaryRepository;

  @GetMapping("/diaries")
  public ResponseEntity<List<Diary>> getAllDiaries(@RequestParam(required = false) String title) {
    try {
      List<Diary> diaries = new ArrayList<Diary>();

      if (title == null)
        diaryRepository.findAll().forEach(diaries::add);
      else
        diaryRepository.findByTitleContaining(title).forEach(diaries::add);

      if (diaries.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(diaries, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/diaries/{id}")
  public ResponseEntity<Diary> getDiaryById(@PathVariable("id") String id) {
    Optional<Diary> diaryData = diaryRepository.findById(id);

    if (diaryData.isPresent()) {
      return new ResponseEntity<>(diaryData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/diaries")
  public ResponseEntity<Diary> createDiary(@RequestBody Diary diary) {
    try {
        Diary _diary = diaryRepository.save(new Diary(diary.getTitle(), diary.getContent(), false));
      return new ResponseEntity<>(_diary, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/diaries/{id}")
  public ResponseEntity<Diary> updateDiary(@PathVariable("id") String id, @RequestBody Diary diary) {
    Optional<Diary> diaryData = diaryRepository.findById(id);

    if (diaryData.isPresent()) {
        Diary _diary = diaryData.get();
      _diary.setTitle(diary.getTitle());
      _diary.setContent(diary.getContent());
      _diary.setPublished(diary.isPublished());
      return new ResponseEntity<>(diaryRepository.save(_diary), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/diaries/{id}")
  public ResponseEntity<HttpStatus> deleteDiary(@PathVariable("id") String id) {
    try {
      diaryRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/diaries")
  public ResponseEntity<HttpStatus> deleteAllDiaries() {
    try {
      diaryRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/diaries/published")
  public ResponseEntity<List<Diary>> findByPublished() {
    try {
      List<Diary> diarys = diaryRepository.findByPublished(true);

      if (diarys.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(diarys, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}