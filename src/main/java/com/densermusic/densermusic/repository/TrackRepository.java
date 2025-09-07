package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByNameIgnoreCase(String name);
    Optional<Track> findByDeezerId(Long deezerId);
}
