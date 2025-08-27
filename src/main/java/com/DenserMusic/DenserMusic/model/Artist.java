package com.DenserMusic.DenserMusic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String urlImage;
    private Integer totalDeezerFans;

    public Artist() {}

    public Artist(String name, String urlImage, Integer totalDeezerFansFans) {
        this.name = name;
        this.urlImage = urlImage;
        this.totalDeezerFans = totalDeezerFansFans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public Integer getTotalDeezerFans() {
        return totalDeezerFans;
    }

    public void setTotalDeezerFans(Integer totalDeezerFans) {
        this.totalDeezerFans = totalDeezerFans;
    }

    @Override
    public String toString() {
        return "Artista: " + name + "\ntotalFans:" + totalDeezerFans;

    }
}
