package com.animatronics.spring.security.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.animatronics.spring.security.mongodb.models.Diary;

public interface DiaryRepository extends MongoRepository<Diary, String> {
  List<Diary> findByVisible(boolean published);
  List<Diary> findByTitleContaining(String title);
  List<Diary> findByOwner(String owner);
}