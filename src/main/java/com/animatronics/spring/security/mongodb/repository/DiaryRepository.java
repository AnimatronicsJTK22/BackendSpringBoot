package com.animatronics.spring.security.mongodb.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.animatronics.spring.security.mongodb.models.Diary;

public interface DiaryRepository extends MongoRepository<Diary, String> {
  List<Diary> findByPublished(boolean published);
  List<Diary> findByTitleContaining(String title);
}