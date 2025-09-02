package com.DenserMusic.DenserMusic.repository;

import com.DenserMusic.DenserMusic.model.Playlist;
import com.DenserMusic.DenserMusic.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByNameIgnoreCase(String name);
    boolean existsByDeezerId(Long deezerId);
}
