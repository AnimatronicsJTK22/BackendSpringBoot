package com.animatronics.spring.security.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "history")
public class History {
    @Id
    private String id;
    private String desc;
    private String balanceDesc;
    private LocalDateTime time;

    public History() {

    }
    
    public History(String id, String desc, String balanceDesc, LocalDateTime time) {
        this.id = id;
        this.desc = desc;
        this.balanceDesc = balanceDesc;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBalanceDesc() {
        return balanceDesc;
    }

    public void setBalanceDesc(String balanceDesc) {
        this.balanceDesc = balanceDesc;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
        String formattedDate = getTime().format(formatter);
        return "History [id=" + id + ", desc=" + desc + ", balanceDesc=" + balanceDesc + ", time=" + formattedDate + "]";
    }
        
}
