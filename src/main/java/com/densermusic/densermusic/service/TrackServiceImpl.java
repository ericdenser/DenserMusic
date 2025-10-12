package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.*;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.trackDTO.CreateTrackRequestDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackSearchResponseDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackSearchResultDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.TrackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TrackServiceImpl implements TrackService {

    private static final Logger logger = LoggerFactory.getLogger(TrackServiceImpl.class);
    private final TrackRepository trackRepository;
    private final DeezerClient deezerClient;
    private final ArtistService artistService;

    public TrackServiceImpl(TrackRepository trackRepository, DeezerClient deezerClient, ArtistServiceImpl artistService) {
        this.trackRepository = trackRepository;
        this.deezerClient = deezerClient;
        this.artistService = artistService;
    }

    @Transactional
    @Override
    public Track save(Track track) {
        if (trackRepository.findByApiId(track.getApiId()).isPresent()) {
            logger.warn("Tentativa de salvar track duplicada com API ID {}", track.getApiId());
            throw new BusinessException("Música com Deezer ID " + track.getApiId() + " já existe.");
        }
        return trackRepository.save(track);
    }

    @Transactional
    @Override
    public void deleteTrackByDbId(Long id) {
        if(!trackRepository.existsById(id)){
            logger.warn("Track (ID {}) não encontrada no Banco de Dados.", id);
            throw new BusinessException("Track com ID " + id + " não encontrado, não foi possível deletar.");
        }

        trackRepository.deleteById(id);
        logger.info("Track (ID {}) deletada do Banco de Dados com sucesso.", id);
    }

    @Transactional
    @Override
    public CreationResultDTO<Track> findOrCreateTrack(CreateTrackRequestDTO request) {

        Long artistApiId = request.artistApiId();// variavel com o api id do artista da track
        Long trackApiId = request.trackApiId(); // variavel com api id da track

        //checa se track ja esta salva
        Optional<Track> trackOptional = trackRepository.findByApiId(trackApiId);

        if (trackOptional.isPresent()) {
            // encontramos a track, nada foi criado (created = false)
            logger.info("Track (API_ID {}) ja existe no Banco de Dados.", trackApiId);
            return new CreationResultDTO<>(trackOptional.get(), false);

        }

        // buscamos os detalhes completos da track na API
        DeezerTrackDTO trackDetailsDto = deezerClient.getTrackById(trackApiId);
        if (trackDetailsDto == null) {
            logger.warn("Track (API_ID: {}) não encontrada na API.", trackApiId);
            throw new BusinessException("Música com API_ID " + trackApiId + " não encontrada na API.");
        }

        //validacao critica para artista sempre condizer com track
        Long correctArtistId = trackDetailsDto.artist().apiId();
        if(!correctArtistId.equals(artistApiId)) {
            logger.warn("Tentativa de relacionamento incorreto. Track(API_ID: {}) não pertence ao Artista(API_ID: {}).", trackApiId, artistApiId);
            throw new BusinessException("Conflito de dados: A música com API_ID " + trackApiId
                    + " pertence ao artista com API_ID " + correctArtistId + ", não ao artista com API_ID " + artistApiId + ".");
        }

        Artist artist = artistService.findOrCreateArtist(new CreateArtistRequestDTO(correctArtistId)).entity();

        Track newTrack = Track.of(trackDetailsDto, artist);

        Track savedTrack = this.save(newTrack);

        logger.info("Track (API_ID {}) e Artista (API_ID {}) salvos no Banco de Dados com sucesso.", trackApiId, correctArtistId);
        return new CreationResultDTO<>(savedTrack, true);

    }

    @Override
    // RETORNA LISTA DE TODAS TRACKS COM NOME CORRESPONDENTE
    public List<DeezerTrackSearchResultDTO> searchTracksByName(String trackName) {
        logger.info("Buscando tracks na API com o nome: '{}'", trackName);
        DeezerTrackSearchResponseDTO response = deezerClient.getTrackByName(trackName);
        if (response != null && response.data() != null && !response.data().isEmpty()) {
            logger.info("Encontradas {} tracks para a busca '{}'", response.data().size(), trackName);
            return response.data();
        }

        logger.info("Nenhuma track encontrada para a busca com o nome '{}'", trackName);
        return Collections.emptyList();
    }

    @Override
    public Optional<Track> findTrackByDbId(Long id) {
        return trackRepository.findById(id);
    }

    @Override
    public Optional<Track> findTrackByApiId(Long apiId) {
        return trackRepository.findByApiId(apiId);
    }

    @Override
    public List<Track> carregarTracksSalvas() {
        return trackRepository.findAll();
    }

}

