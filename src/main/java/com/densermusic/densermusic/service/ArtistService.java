package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistService {

    Optional<Artist> fetchArtistFromApi(Long artistApiId);

    List<DeezerArtistDTO> searchArtistsByName(String nome);

    Optional<Artist> findByApiId(Long apiId);

    Optional<Artist> findByDbId(Long id);

    Artist save(Artist artist);

    CreationResultDTO<Artist> findOrCreateArtist(CreateArtistRequestDTO request);

    List<Artist> carregarArtistasSalvos();

    void deleteArtistByDbId(Long id);
}
