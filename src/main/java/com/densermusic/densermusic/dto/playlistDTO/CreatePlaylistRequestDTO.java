package com.densermusic.densermusic.dto.playlistDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlaylistRequestDTO(
        @NotBlank(message = "O nome da playlist é obrigatório.")
        @Size(max = 100, message = "O nome da playlist deve ter no máximo 50 caracteres.")
        String name
) {}
