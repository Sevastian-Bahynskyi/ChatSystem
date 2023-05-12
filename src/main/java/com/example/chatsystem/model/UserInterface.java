package com.example.chatsystem.model;
import javafx.scene.image.Image;

public interface UserInterface
{
  Image getImage();
  String getUsername();
  String getPassword();
  String getImageUrl();
  void setImageUrl(String imageUrl);
  boolean equals(Object obj);

  String getViaId();
}
