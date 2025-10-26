package com.densermusic.densermusic.mapper;

import com.densermusic.densermusic.dto.trackDTO.TrackResponseDTO;
import com.densermusic.densermusic.model.Track;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrackMapper {

    private final ArtistMapper artistMapper;

    public TrackMapper(ArtistMapper artistMapper) {
        this.artistMapper = artistMapper;
    }

    public TrackResponseDTO toDTO(Track track) {
        if (track == null) return null;

        return new TrackResponseDTO(
                track.getId(),
                track.getApiId(),
                track.getName(),
                track.getAlbum(),
                track.getDurationInSeconds(),
                track.getRank(),
                track.getReleaseDate(),
                artistMapper.toDTO(track.getArtist())
        );
    }

    public List<TrackResponseDTO> toDTOList(List<Track> tracks) {
        return tracks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}