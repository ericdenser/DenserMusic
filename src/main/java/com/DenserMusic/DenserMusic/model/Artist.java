package com.DenserMusic.DenserMusic.model;

import jakarta.persistence.*;

import java.util.Objects;

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

    @Override // garantir a igualdade correta de objetos
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(id, artist.id);
    }

    @Override // garantir a igualdade correta de objetos
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
