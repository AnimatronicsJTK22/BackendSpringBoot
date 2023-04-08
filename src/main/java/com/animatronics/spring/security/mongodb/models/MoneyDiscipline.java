package com.animatronics.spring.security.mongodb.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "moneydiscipline")
public class MoneyDiscipline {
  @Id
  private String id;
  private boolean balance;
  private String owner;
  private LocalDateTime timeUpdated;

  public MoneyDiscipline() {

  }

  public MoneyDiscipline(Boolean balance, String owner, LocalDateTime timeUpdated) {
    this.balance = balance;
    this.owner = owner;
    this.timeUpdated = timeUpdated;
  }

  

  @Override
  public String toString() {
    return "MoneyDiscipline [id=" + id + ", balance=" + balance + ", owner="
        + owner + ", timeUpdated=" + timeUpdated + "]";
  }
}