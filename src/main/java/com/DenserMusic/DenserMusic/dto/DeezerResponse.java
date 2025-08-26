package com.DenserMusic.DenserMusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerResponse(List<DeezerArtist> data) {
}
