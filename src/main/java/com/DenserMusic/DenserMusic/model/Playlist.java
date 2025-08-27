package com.DenserMusic.DenserMusic.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "playlist_musics",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "music_id")
    )

    List<Track> musicsOfPlaylist = new ArrayList<>();

    public Playlist() {}




    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Track> getMusicsOfPlaylist() {
        return musicsOfPlaylist;
    }

    public void setMusicsOfPlaylist(List<Track> musicsOfPlaylist) {
        this.musicsOfPlaylist = musicsOfPlaylist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
