package com.densermusic.densermusic.dto.playlistDTO;

import jakarta.validation.constraints.NotNull;

public record AddTrackRequestDTO(
        @NotNull(message = "O campo trackId é obrigatório.")
        Long trackId
) {}
