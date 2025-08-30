package com.DenserMusic.DenserMusic.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "musics")
public class Track {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne //varias tracks para um artista
    private Artist artist;

    private String name;
    private String album;
    private Integer durationInSeconds;
    private Integer rank;
    private LocalDate releaseDate;

    public Track() {}

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Integer getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(Integer durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        String nomeArtista = (artist != null) ? artist.getName() : "Desconhecido";
        return "Música: " + name + " | Artista: " + nomeArtista + " | Álbum: " + album;
    }


    @Override // garantir a igualdade correta de objetos
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override // garantir a igualdade correta de objetos
    public int hashCode() {
        return Objects.hashCode(id);
    }


}
