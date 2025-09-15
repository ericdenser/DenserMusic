package com.densermusic.densermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackDTO(
        String title,
        int duration,
        int rank,
        @JsonProperty("id") Long deezerId,
        @JsonProperty("release_date") String releaseDate,
        DeezerAlbumDTO album,
        DeezerArtistDTO artist
){

}
