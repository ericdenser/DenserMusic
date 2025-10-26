package com.densermusic.densermusic.dto.trackDTO;

import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerTrackDTO(

        String title,

        int duration,

        int rank,

        @JsonProperty("id") Long apiId,

        @JsonProperty("release_date") String releaseDate,

        DeezerAlbumDTO album,

        DeezerArtistDTO artist
) {}
