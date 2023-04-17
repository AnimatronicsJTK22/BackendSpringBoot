package com.animatronics.spring.security.mongodb.models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "moneydiscipline")
public class MoneyDiscipline {
  @Id
  private String id;
  private double balance;
  private String owner;

  public MoneyDiscipline() {

  }

  public MoneyDiscipline(double balance, String owner, LocalDateTime timeUpdated) {
    this.balance = balance;
    this.owner = owner;
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

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  @Override
  public String toString() {
    return "MoneyDiscipline [id=" + id + ", balance=" + balance + ", owner="
        + owner + "]";
  }
}