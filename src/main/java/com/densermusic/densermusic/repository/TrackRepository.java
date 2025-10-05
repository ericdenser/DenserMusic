package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Long> {
    Optional<Track> findByNameIgnoreCase(String name);
    Optional<Track> findByApiId(Long apiId);

    @Query("SELECT t FROM Track t WHERE t NOT IN (SELECT pt FROM Playlist p JOIN p.tracksOfPlaylist pt WHERE p.id = :playlistId)")
    List<Track> findTracksNotInPlaylist(@Param("playlistId") Long playlistId);
}
