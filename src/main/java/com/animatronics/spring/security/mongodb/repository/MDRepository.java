package com.animatronics.spring.security.mongodb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.animatronics.spring.security.mongodb.models.MoneyDiscipline;

public interface MDRepository extends MongoRepository<MoneyDiscipline, String> {
  List<MoneyDiscipline> findByOwner(String owner);

  Optional<MoneyDiscipline> findById(String id);
}