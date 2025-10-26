package com.densermusic.densermusic.dto.playlistDTO;


import com.densermusic.densermusic.dto.trackDTO.TrackResponseDTO;
import com.densermusic.densermusic.model.Track;

import java.util.List;

public record PlaylistResponseDTO(
        Long id,
        String name
) {
}
