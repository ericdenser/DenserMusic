package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.playlistDTO.*;
import com.densermusic.densermusic.mapper.PlaylistMapper;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.service.PlaylistService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final PlaylistMapper playlistMapper;

    public PlaylistController(PlaylistService playlistService, PlaylistMapper playlistMapper) {
        this.playlistService = playlistService;
        this.playlistMapper = playlistMapper;
    }

    @GetMapping
    public ResponseEntity<List<PlaylistResponseDTO>> getAllPlaylists() {
        List<Playlist> playlists = playlistService.loadSavedPlaylists();
        return ResponseEntity.ok(playlistMapper.toDTOList(playlists));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDetailsDTO> getPlaylistById(@PathVariable Long id) {
        return ResponseEntity.of(playlistService.findDetailsById(id));
    }

    @PostMapping
    public ResponseEntity<PlaylistResponseDTO> createPlaylist(@RequestBody @Valid CreatePlaylistRequestDTO request) {
        Playlist playlist = playlistService.createPlaylist(request.name());
        PlaylistResponseDTO dto = playlistMapper.toDTO(playlist);
        URI location = URI.create("/api/playlists/" + dto.id());
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponseDTO> updatePlaylistName(@PathVariable Long id, @RequestBody @Valid UpdatePlaylistRequestDTO request) {
        Playlist updatedPlaylist = playlistService.updatePlaylistName(id, request.newName());
        return ResponseEntity.ok(playlistMapper.toDTO(updatedPlaylist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistId}/tracks")
    public ResponseEntity<PlaylistDetailsDTO> addTrackToPlaylist(@PathVariable Long playlistId, @RequestBody @Valid AddTrackRequestDTO request) {
        Playlist updated = playlistService.addTrackInPlaylist(playlistId, request.trackId());
        return ResponseEntity.ok(playlistMapper.toDetailsDTO(updated));
    }

    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> deleteTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.noContent().build();
    }
}
