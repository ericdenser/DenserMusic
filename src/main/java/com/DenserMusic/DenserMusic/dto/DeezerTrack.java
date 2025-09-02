package com.DenserMusic.DenserMusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrack(
        String title,
        int duration,
        int rank,
        @JsonProperty("id") long deezerId,
        @JsonProperty("release_date") String releaseDate,
        DeezerSimpleAlbum album,
        DeezerSimpleArtist artist
){

}
