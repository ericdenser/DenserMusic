package com.densermusic.densermusic.dto.trackDTO;

import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackSearchResultDTO(
        @JsonProperty("id") Long apiId,
        String title,
        DeezerArtistDTO artist) {
}
