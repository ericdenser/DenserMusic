package com.densermusic.densermusic.dto.trackDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateTrackRequestDTO(
        @NotNull(message = "O campo trackApiId é obrigatório.")
        Long trackApiId,

        @NotNull(message = "O campo artistApiId é obrigatório.")
        Long artistApiId
) {}
