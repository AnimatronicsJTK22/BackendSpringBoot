package com.animatronics.spring.security.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.animatronics.spring.security.mongodb.models.History;

public interface HistoryRepository extends MongoRepository<History, String> {

    
    
}
