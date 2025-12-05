package com.example.cloudgallery;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String s3Url;
    private LocalDateTime uploadDate;

    public Image() {
    }

    public Image(String title, String description, String s3Url) {
        this.title = title;
        this.description = description;
        this.s3Url = s3Url;
        this.uploadDate = LocalDateTime.now();
    }

    // Getters e Setters manuais (caso o Lombok falhe)
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getS3Url() {
        return s3Url;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
}
