package com.densermusic.densermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackSearchResult(
        @JsonProperty("id") Long id,
        String title,
        DeezerArtist artist

) {
}
