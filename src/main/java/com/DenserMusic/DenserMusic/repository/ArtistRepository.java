package com.DenserMusic.DenserMusic.repository;

import com.DenserMusic.DenserMusic.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByNameIgnoreCase(String name);
}
