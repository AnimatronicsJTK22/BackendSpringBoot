package com.animatronics.spring.security.mongodb.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "diaries")
public class Diary {
  @Id
  private String id;
  private String title;
  private String content;
  private boolean visibility;
  private String owner;
  private LocalDateTime time;

  public Diary() {

  }

  public Diary(String title, String content, boolean visibility, String owner, LocalDateTime time) {
    this.title = title;
    this.content = content;
    this.visibility = visibility;
    this.owner = owner;
    this.time = time;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public boolean isVisibility() {
    return visibility;
  }

  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }

  public String getOwner() {
    return this.owner;
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
    String formattedDate = getTime().format(formatter);
    return "Diary [id=" + id + ", title=" + title + ", content=" + content + ", visibility=" + visibility + ", owner="
        + owner + ", time=" + formattedDate + "]";
  }
}