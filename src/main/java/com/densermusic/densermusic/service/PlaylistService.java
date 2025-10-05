package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;

import java.util.List;
import java.util.Optional;

public interface PlaylistService {

    Playlist criarPlaylist(String nome);

    void deletarPlaylist(Long playlistId);

    Playlist updatePlaylistName(Long playlistId, String nome);

    Playlist adicionarTrackNaPlaylist(Long playlistId, Long trackId);

    Playlist removerTrackDaPlaylist(Long playlistId, Long trackId);

    List<Playlist> carregarPlaylistsSalvas();

    Optional<Playlist> buscarPlaylist(Long playlistId);

    Optional<PlaylistDetailsDTO> findDetailsById(Long id);

    List<Track> findAvailableTracksForPlaylist(Long playlistId);

}
