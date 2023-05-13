package com.animatronics.spring.security.mongodb.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "moneydiscipline")
public class MoneyDiscipline {
  @Id
  private String id;
  private double balance;
  private String[] lastChangeDesc;
  private String owner;
  private LocalDateTime time;

  public MoneyDiscipline() {

  }

  public MoneyDiscipline(String id, double balance, String[] lastChangeDesc, String owner, LocalDateTime timeUpdated) {
    this.id = id;
    this.balance = balance;
    this.lastChangeDesc = lastChangeDesc;
    this.owner = owner;
    this.time = timeUpdated;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(double balance) {
    this.balance = balance;
  }

  public String[] getLastChangeDesc() {
    return this.lastChangeDesc;
  }

  public void setLastChangeDesc(String[] lastChangeDesc) {
    this.lastChangeDesc = lastChangeDesc;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }
  
  public LocalDateTime getTime() {
    return this.time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }


  @Override
  public String toString() {
    return "MoneyDiscipline [id=" + id + ", balance=" + balance + ", lastChangeDesc=" + lastChangeDesc + ", owner="
        + owner + "]";
  }
}