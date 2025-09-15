package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.DeezerArtistDTO;
import com.densermusic.densermusic.dto.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final DeezerClient deezerClient;

    public ArtistService(ArtistRepository artistRepository, DeezerClient deezerClient) {
        this.artistRepository = artistRepository;
        this.deezerClient = deezerClient;
    }

    //SALVA UM ARTISTA NO BANCO (getFirst)
    private Optional<Artist> fetchArtistFromDeezerApi(Long artistDeezerId) {
        DeezerArtistDTO deezerArtistDto = deezerClient.searchArtistById(artistDeezerId);

        return Optional.ofNullable(deezerArtistDto)
                .map(Artist::of);
    }

    //RETORNA LISTA DE TODOS OS ARTISTAS COM O NOME CORRESPONDENTE
    public List<DeezerArtistDTO> searchArtistsByName(String nome) {
        DeezerArtistSearchResponseDTO response = deezerClient.searchArtistByName(nome);

        if (response != null && response.data() != null) {
            return response.data();
        }
        return Collections.emptyList();
    }

    public Optional<Artist> findByDeezerId(Long deezerId) {
        return artistRepository.findByDeezerId(deezerId);
    }

    public Optional<Artist> findByDbId(Long id) {
        return artistRepository.findById(id);
    }

    public Artist save(Artist artist) {
        if (artistRepository.findByDeezerId(artist.getDeezerId()).isPresent()) {
            throw new BusinessException("Artista com Deezer ID " + artist.getDeezerId() + " já existe.");
        }
        return artistRepository.save(artist);
    }

    public Artist findOrCreateArtist(Long artistDeezerId) {

        //procura se já está salvo
        return artistRepository.findByDeezerId(artistDeezerId)
                .orElseGet(() -> {

                    // nao salvo, procura na api
                    Optional<Artist> artistOptional = fetchArtistFromDeezerApi(artistDeezerId);

                    return artistOptional.map(this::save)
                            .orElseThrow(() -> new BusinessException("Não foi possível encontrar o " +
                                    "artista no Deezer com o ID: " + artistDeezerId));
                });
    }

    public List<Artist> carregarArtistasSalvos() {
        return artistRepository.findAll();
    }
}

