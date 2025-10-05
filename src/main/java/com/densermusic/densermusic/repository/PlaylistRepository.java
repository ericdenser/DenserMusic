package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByNameIgnoreCase(String name);

    @Query("SELECT p FROM Playlist p JOIN FETCH p.tracksOfPlaylist WHERE p.id = :id")
    //Busque a playlist E, na mesma viagem ao banco, já traga junto a sua lista de musicas
    Optional<Playlist> findByIdWithTracks(@Param("id") Long id);

}
