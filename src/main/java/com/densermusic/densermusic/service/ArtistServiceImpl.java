package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.*;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;
    private final DeezerClient deezerClient;

    public ArtistServiceImpl(ArtistRepository artistRepository, DeezerClient deezerClient) {
        this.artistRepository = artistRepository;
        this.deezerClient = deezerClient;
    }

    //SALVA UM ARTISTA NO BANCO (getFirst)
    public Optional<Artist> fetchArtistFromApi(Long artistApiId) {
        DeezerArtistDTO ApiArtistDto = deezerClient.searchArtistById(artistApiId);

        return Optional.ofNullable(ApiArtistDto)
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

    public Optional<Artist> findByApiId(Long apiID) {
        return artistRepository.
                findByApiId(apiID);
    }

    public Optional<Artist> findByDbId(Long id) {
        return artistRepository.findById(id);
    }

    public Artist save(Artist artist) {
        if (artistRepository.findByApiId(artist.getApiId()).isPresent()) {
            throw new BusinessException("Artista com Deezer ID " + artist.getApiId() + " já existe.");
        }
        return artistRepository.save(artist);
    }

    public CreationResultDTO<Artist> findOrCreateArtist(CreateArtistRequestDTO request) {
        //procura se já está salvo
        Optional<Artist> artistOptional = artistRepository.findByApiId(request.apiId());
        if (artistOptional.isPresent()) {
            // encontramos o artista, nada foi criado (created = false)
            return new CreationResultDTO<>(artistOptional.get(), false);
        }

        // nao salvo, procura na api
        Optional<Artist> newaArtistOptional = fetchArtistFromApi(request.apiId());

        // se chegou a esta linha, significa que o artista precisa ser criado
        Artist savedArtist = newaArtistOptional.map(this::save)
                .orElseThrow(() -> new BusinessException("Não foi possível encontrar o "
                        + "Artista no Deezer com o ID: " + request.apiId()));

        // artista criado e salvo (created = true)
        return new CreationResultDTO<>(savedArtist, true);
    }

    public List<Artist> carregarArtistasSalvos() {
        return artistRepository.findAll();
    }

    public void deleteArtistByDbId(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new BusinessException("Artista com ID " + id + " não encontrado, não foi possível deletar.");
        }
            artistRepository.deleteById(id);
    }
}

