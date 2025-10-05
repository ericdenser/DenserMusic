package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.*;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final DeezerClient deezerClient;
    private final ArtistService artistService;

    public TrackService(TrackRepository trackRepository, DeezerClient deezerClient, ArtistService artistService) {
        this.trackRepository = trackRepository;
        this.deezerClient = deezerClient;
        this.artistService = artistService;
    }

    public Track save(Track track) {
        if (trackRepository.findByDeezerId(track.getDeezerId()).isPresent()) {
            throw new BusinessException("Música com Deezer ID " + track.getDeezerId() + " já existe.");
        }
        return trackRepository.save(track);
    }

    public CreationResultDTO<Track> findOrCreateTrack(CreateTrackRequestDTO request) {

        Long artistDeezerId = request.artistDeezerId();// variavel com o id do artista da track
        Long trackDeezerId = request.trackDeezerId(); // variavel com id da track

        //checa se track ja esta salva
        Optional<Track> trackOptional = trackRepository.findByDeezerId(trackDeezerId);

        if (trackOptional.isPresent()) {
            // encontramos a track, nada foi criado (created = false)
            return new CreationResultDTO<>(trackOptional.get(), false);
        }

        // buscamos os detalhes completos da track na API
        DeezerTrackDTO trackDetailsDto = deezerClient.getTrackById(trackDeezerId);
        if (trackDetailsDto == null) {
            throw new BusinessException("Música com ID " + trackDeezerId + " não encontrada no Deezer.");
        }

        //validacao critica para artista sempre condizer com track
        Long correctArtistId = trackDetailsDto.artist().deezerId();
        if(!correctArtistId.equals(artistDeezerId)) {
            throw new BusinessException("Conflito de dados: A música com ID " + trackDeezerId
                    + " pertence ao artista com ID " + correctArtistId + ", não ao artista com ID " + artistDeezerId + ".");
        }

        Artist artist = artistService.findOrCreateArtist(new CreateArtistRequestDTO(correctArtistId)).entity();

        Track newTrack = Track.of(trackDetailsDto, artist);

        Track savedTrack = this.save(newTrack);

        return new CreationResultDTO<>(savedTrack, true);
    }

    // RETORNA LISTA DE TODAS TRACKS COM NOME CORRESPONDENTE
    public List<DeezerTrackSearchResultDTO> searchTracksByName(String trackName) {
        DeezerTrackSearchResponseDTO response = deezerClient.getTrackByName(trackName);
        if (response != null && response.data() != null) {
            return response.data();
        }
        return Collections.emptyList();
    }

    public Optional<Track> findTrackByDbId(Long id) {
        return trackRepository.findById(id);
    }

    public Optional<Track> findTrackByDeezerId (Long deezerId) {
        return trackRepository.findByDeezerId(deezerId);
    }

    public List<Track> carregarTracksSalvas() {
        return trackRepository.findAll();
    }

    public void deleteTrackByDbId(Long id) {
        if(!trackRepository.existsById(id)){
            throw new BusinessException("Track com ID " + id + " não encontrado, não foi possível deletar.");
        }
        trackRepository.deleteById(id);
    }
}

