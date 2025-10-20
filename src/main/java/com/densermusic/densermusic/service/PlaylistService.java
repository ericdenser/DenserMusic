package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;

import java.util.List;
import java.util.Optional;

public interface PlaylistService {

    Playlist createPlaylist(String nome);

    void deletePlaylist(Long playlistId);

    Playlist updatePlaylistName(Long playlistId, String nome);

    Playlist addTrackInPlaylist(Long playlistId, Long trackId);

    Playlist removeTrackFromPlaylist(Long playlistId, Long trackId);

    List<Playlist> loadSavedPlaylists();

    Optional<Playlist> searchPlaylist(Long playlistId);

    Optional<PlaylistDetailsDTO> findDetailsById(Long id);

    List<Track> findAvailableTracksForPlaylist(Long playlistId);

}
