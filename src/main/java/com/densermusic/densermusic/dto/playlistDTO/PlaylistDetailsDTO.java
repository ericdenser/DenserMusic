package com.densermusic.densermusic.dto.playlistDTO;

import com.densermusic.densermusic.model.Track;

import java.util.List;

public record PlaylistDetailsDTO(Long id, String nome, List<Track> tracks) {
}
