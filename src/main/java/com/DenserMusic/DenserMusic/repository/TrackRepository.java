package com.DenserMusic.DenserMusic.repository;

import com.DenserMusic.DenserMusic.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
}
