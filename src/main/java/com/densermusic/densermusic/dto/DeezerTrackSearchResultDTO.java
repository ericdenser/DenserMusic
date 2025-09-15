package com.densermusic.densermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackSearchResultDTO(
        @JsonProperty("id") Long deezerId,
        String title,
        DeezerArtistDTO artist

) {
}
