package com.densermusic.densermusic.dto.artistDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateArtistRequestDTO(
        @NotNull(message = "O campo apiId é obrigatório.")
        Long apiId
) { }
