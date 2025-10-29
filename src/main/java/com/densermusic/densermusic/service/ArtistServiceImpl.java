package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.*;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistServiceImpl implements ArtistService {

    private static final Logger logger = LoggerFactory.getLogger(ArtistServiceImpl.class);
    private final ArtistRepository artistRepository;
    private final DeezerClient deezerClient;

    public ArtistServiceImpl(ArtistRepository artistRepository, DeezerClient deezerClient) {
        this.artistRepository = artistRepository;
        this.deezerClient = deezerClient;
    }

    @Override
    public Optional<Artist> fetchArtistFromApi(Long artistApiId) {
        DeezerArtistDTO ApiArtistDto = deezerClient.searchArtistById(artistApiId);

        return Optional.ofNullable(ApiArtistDto)
                .map(Artist::of);
    }

    //RETORNA LISTA DE TODOS OS ARTISTAS COM O NOME CORRESPONDENTE
    @Override
    public List<DeezerArtistDTO> searchArtistsByName(String artistName) {
        logger.info("Buscando artistas na API com o nome: '{}'", artistName);
        DeezerArtistSearchResponseDTO response = deezerClient.searchArtistByName(artistName);

        if (response != null && response.data() != null && !response.data().isEmpty()) {
            logger.info("Encontrados {} artistas para a busca '{}'", response.data().size(), artistName);
            return response.data();
        }

        logger.info("Nenhum artista encontrado para a busca '{}'", artistName);
        return Collections.emptyList();
    }

    @Override
    public Optional<Artist> findByApiId(Long apiID) {
        return artistRepository.
                findByApiId(apiID);
    }

    @Override
    public Optional<Artist> findByDbId(Long id) {
        return artistRepository.findById(id);
    }

    @Transactional
    @Override
    public Artist save(Artist artist) {
        if (artistRepository.findByApiId(artist.getApiId()).isPresent()) {
            logger.warn("Tentativa de salvar artista duplicado com API ID {}", artist.getApiId());
            throw new BusinessException("Artista com API ID " + artist.getApiId() + " já existe.");
        }

        logger.info("Salvando novo artista no banco de dados: {}", artist.getName());
        return artistRepository.save(artist);
    }

    @Transactional
    @Override
    public CreationResultDTO<Artist> findOrCreateArtist(CreateArtistRequestDTO request) {
        //procura se já está salvo
        Optional<Artist> artistOptional = artistRepository.findByApiId(request.apiId());
        if (artistOptional.isPresent()) {
            // encontramos o artista, nada foi criado (created = false)
            logger.info("Artista com API ID {} já existe no banco de dados.", request.apiId());
            return new CreationResultDTO<>(artistOptional.get(), false);
        }

        // nao salvo, procura na api
        Optional<Artist> newArtistOptional = fetchArtistFromApi(request.apiId());

        // se chegou a esta linha, significa que o artista precisa ser criado
        Artist savedArtist = newArtistOptional.map(this::save)
                .orElseThrow(() -> {
                            logger.warn("Tentativa de criar artista com API ID {} falhou. Artista não encontrado.", request.apiId());
                            return new BusinessException("Não foi possível encontrar o "
                                    + "Artista na API com o ID: " + request.apiId());
                        });

        // artista criado e salvo (created = true)
        logger.info("Novo artista '{}'(API ID: {}) foi criado e salvo no banco.", savedArtist.getName(), savedArtist.getApiId());
        return new CreationResultDTO<>(savedArtist, true);
    }

    @Override
    public List<Artist> loadSavedArtists() {
        return artistRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteArtistByDbId(Long id) {
        if (!artistRepository.existsById(id)) {
            logger.warn("Tentativa de deletar artista com ID de banco {} falhou. Artista não encontrado.", id);
            throw new BusinessException("Artista com ID " + id + " não encontrado, não foi possível deletar.");
        }
        artistRepository.deleteById(id);
        logger.info("Artista com ID {} deletado com sucesso.", id);
    }
}

