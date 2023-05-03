package com.animatronics.spring.security.mongodb.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.animatronics.spring.security.mongodb.models.Diary;

public interface DiaryRepository extends MongoRepository<Diary, String> {
  List<Diary> findByVisibility(boolean visibility);

  List<Diary> findByTitleContaining(String title);

  List<Diary> findByOwner(String owner);

  List<Diary> findByOwner(String owner, Sort by);
}