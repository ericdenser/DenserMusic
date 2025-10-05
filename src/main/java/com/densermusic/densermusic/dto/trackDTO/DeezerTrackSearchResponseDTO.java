package com.densermusic.densermusic.dto.trackDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackSearchResponseDTO(List<DeezerTrackSearchResultDTO> data) {
}
