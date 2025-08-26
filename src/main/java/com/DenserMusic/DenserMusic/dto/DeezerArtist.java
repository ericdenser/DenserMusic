package com.DenserMusic.DenserMusic.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerArtist(String name,
                           String picture,
                           @JsonAlias("nb_fan") Integer totalFas) {

}
