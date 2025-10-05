package com.densermusic.densermusic.dto.artistDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerArtistSearchResponseDTO(List<DeezerArtistDTO> data) {
}
