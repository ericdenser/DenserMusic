package com.DenserMusic.DenserMusic.repository;

import com.DenserMusic.DenserMusic.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByNameIgnoreCase(String name);
}
