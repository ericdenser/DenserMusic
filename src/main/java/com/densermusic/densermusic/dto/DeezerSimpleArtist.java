package com.densermusic.densermusic.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerSimpleArtist(String name,
                                 @JsonProperty("id") Long id) {
}
