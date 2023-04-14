package com.animatronics.spring.security.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "history")
public class History {
    @Id
    private String id;
    private String desc;
    private String balanceDesc;
        
}
