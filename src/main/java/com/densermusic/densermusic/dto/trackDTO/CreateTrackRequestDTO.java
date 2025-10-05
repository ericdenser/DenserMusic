package com.densermusic.densermusic.dto.trackDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateTrackRequestDTO(Long trackApiId, Long artistApiId) {
}
