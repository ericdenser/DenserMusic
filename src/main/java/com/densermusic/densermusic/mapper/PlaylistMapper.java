package com.densermusic.densermusic.mapper;

import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.dto.playlistDTO.PlaylistResponseDTO;
import com.densermusic.densermusic.dto.trackDTO.TrackResponseDTO;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlaylistMapper {

    private final TrackMapper trackMapper;

    public PlaylistMapper(TrackMapper trackMapper) {
        this.trackMapper = trackMapper;
    }

    public PlaylistResponseDTO toDTO(Playlist playlist) {
        if (playlist == null) return null;

        return new PlaylistResponseDTO(
                playlist.getId(),
                playlist.getName()
        );
    }

    public PlaylistDetailsDTO toDetailsDTO(Playlist playlist) {
        if (playlist == null) return null;

        // o mapeamento da lista de tracks só acontece se a lista for carregada de propósito pelo serviço.
        List<TrackResponseDTO> trackDTOs = playlist.getTracksOfPlaylist() == null ? null :
                trackMapper.toDTOList(playlist.getTracksOfPlaylist());

        return new PlaylistDetailsDTO(
                playlist.getId(),
                playlist.getName(),
                trackDTOs
        );
    }

    public List<PlaylistResponseDTO> toDTOList(List<Playlist> playlist) {
        return playlist.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
