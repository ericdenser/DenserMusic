package com.densermusic.densermusic.mapper;

import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.model.Artist;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component // bean
public class ArtistMapper {

    public ArtistResponseDTO toDTO(Artist artist) {
        if (artist == null) {
            return null;
        }
        return new ArtistResponseDTO(
                artist.getId(),
                artist.getApiId(),
                artist.getName(),
                artist.getUrlImage(),
                artist.getTotalDeezerFans()
        );
    }

    public List<ArtistResponseDTO> toDTOList(List<Artist> artists) {
        return artists.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
