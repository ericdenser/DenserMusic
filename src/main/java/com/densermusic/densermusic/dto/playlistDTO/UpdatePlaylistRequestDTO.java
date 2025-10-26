package com.densermusic.densermusic.dto.playlistDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePlaylistRequestDTO(
        @NotBlank(message = "O novo nome da playlist é obrigatório.")
        @Size(max = 100, message = "O novo nome da playlist deve ter no máximo 50 caracteres.")
        String newName
) {}
