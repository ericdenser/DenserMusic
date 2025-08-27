package com.DenserMusic.DenserMusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackSearchResult(
        long id,
        String title,
        DeezerSimpleArtist artist

) {
}
