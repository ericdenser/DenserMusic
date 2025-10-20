package com.densermusic.densermusic.controller;


import com.densermusic.densermusic.dto.playlistDTO.AddTrackRequestDTO;
import com.densermusic.densermusic.dto.playlistDTO.CreatePlaylistRequestDTO;
import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.dto.playlistDTO.UpdatePlaylistRequestDTO;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.service.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;


    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/search")
    public List<Playlist> getAllPlaylists() {
        return playlistService.loadSavedPlaylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDetailsDTO> getPlaylistById (@PathVariable Long id) {
        return ResponseEntity.of(playlistService.findDetailsById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Playlist createPlaylist(@RequestBody CreatePlaylistRequestDTO request) {
        return playlistService.createPlaylist(request.name());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylistName(@PathVariable Long id, @RequestBody UpdatePlaylistRequestDTO request) {
        Playlist updatedPlaylist = playlistService.updatePlaylistName(id, request.newName());
        return ResponseEntity.ok(updatedPlaylist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{playlistId}/tracks")
    @ResponseStatus(HttpStatus.OK)
    public Playlist addTrackToPlaylist(@PathVariable Long playlistId, @RequestBody AddTrackRequestDTO request) {
        return playlistService.addTrackInPlaylist(playlistId, request.trackId());
    }

    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<Void> deleteTrackFromPlaylist(@PathVariable Long playlistId, @PathVariable Long trackId) {
        playlistService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.noContent().build();
    }

}
