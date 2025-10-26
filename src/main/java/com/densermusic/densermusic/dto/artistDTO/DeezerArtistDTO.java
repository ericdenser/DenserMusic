package com.densermusic.densermusic.dto.artistDTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerArtistDTO(
                              String name,

                              String picture,

                              @JsonProperty("id") Long apiId,

                              @JsonAlias("nb_fan") Integer totalFas) {

}
