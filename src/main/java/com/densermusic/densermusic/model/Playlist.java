package com.densermusic.densermusic.model;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    //varias tracks na playlist, e varias playlists com a track
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY) // cascade e fetchType há serem avaliados
    @JoinTable(
            name = "playlist_tracks",
            joinColumns = @JoinColumn(name = "playlist_id"), // coluna que referencia o ID da playlist na tabela intermediária.
            inverseJoinColumns = @JoinColumn(name = "track_id") // coluna que referencia o ID da track na tabela intermediária.
    )
    List<Track> tracksOfPlaylist = new ArrayList<>();

    public Playlist() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Track> getTracksOfPlaylist() {
        return tracksOfPlaylist;
    }

    public void setTracksOfPlaylist(List<Track> musicsOfPlaylist) {
        this.tracksOfPlaylist = musicsOfPlaylist;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Playlist: " + name + " (" + getTracksOfPlaylist().size() + " músicas)";
    }

    @Override// garantir a igualdade correta de objetos
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override// garantir a igualdade correta de objetos
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
