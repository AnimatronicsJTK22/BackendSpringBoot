package com.animatronics.spring.security.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "diaries")
public class Diary {
  @Id
  private String id;

  private String title;
  private String content;
  private boolean published;

  public Diary() {

  }

  public Diary(String title, String content, boolean published) {
    this.title = title;
    this.content = content;
    this.published = published;
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

  public boolean isPublished() {
    return published;
  }

  public void setPublished(boolean isPublished) {
    this.published = isPublished;
  }

  @Override
  public String toString() {
    return "Diary [id=" + id + ", title=" + title + ", content=" + content + ", published=" + published + "]";
  }
}