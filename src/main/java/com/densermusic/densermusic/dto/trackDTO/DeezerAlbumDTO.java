package com.densermusic.densermusic.dto.trackDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerAlbumDTO(String title) {
}
