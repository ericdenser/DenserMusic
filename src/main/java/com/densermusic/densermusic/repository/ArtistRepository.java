package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByApiId(Long apiId);

    Optional<Artist> findByNameIgnoreCase(String name);

    Optional<List<Artist>> findByNameContainingIgnoreCase(String name);
}
