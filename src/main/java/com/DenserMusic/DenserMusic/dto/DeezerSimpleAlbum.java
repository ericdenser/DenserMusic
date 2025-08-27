package com.DenserMusic.DenserMusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeezerSimpleAlbum(String title) {
}
