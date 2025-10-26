package com.densermusic.densermusic.dto.trackDTO;

import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import java.time.LocalDate;

public record TrackResponseDTO(
        Long id,
        Long apiId,
        String name,
        String album,
        Integer durationInSeconds,
        Integer rank,
        LocalDate releaseDate,
        ArtistResponseDTO artist
) {}