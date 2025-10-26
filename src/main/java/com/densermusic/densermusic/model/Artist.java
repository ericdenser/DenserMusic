package com.densermusic.densermusic.model;

import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;

@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true, nullable = false)
    private Long apiId;

    @Column(unique = true)
    private String name;

    private String urlImage;
    private Integer totalDeezerFans;

    public Artist() {}

    public Artist(String name, String urlImage, Integer totalDeezerFansFans, Long deezerId) {
        this.name = name;
        this.urlImage = urlImage;
        this.totalDeezerFans = totalDeezerFansFans;
        this.apiId = deezerId;
    }

    public static Artist of(DeezerArtistDTO deezerArtistDTO) {
        Artist newArtist = new Artist();

        newArtist.setName(deezerArtistDTO.name());
        newArtist.setUrlImage(deezerArtistDTO.picture()); // Usa o objeto Artist que já garantimos que existe
        newArtist.setApiId(deezerArtistDTO.apiId());
        newArtist.setTotalDeezerFans(deezerArtistDTO.totalFas());

        return newArtist;
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

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "Artista: " + name + " (Fãs: " + totalDeezerFans + ")";
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
