package com.densermusic.densermusic.dto.artistDTO;

public record ArtistResponseDTO(
        Long id,
        Long apiId,
        String name,
        String urlImagem,
        Integer totalDeezerFans
) {
}
