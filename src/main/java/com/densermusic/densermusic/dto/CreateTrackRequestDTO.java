package com.densermusic.densermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateTrackRequestDTO(Long trackDeezerId, Long artistDeezerId) {
}
