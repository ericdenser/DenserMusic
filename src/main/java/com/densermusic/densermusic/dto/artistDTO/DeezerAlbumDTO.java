package com.densermusic.densermusic.dto.artistDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerAlbumDTO(String title) {
}
