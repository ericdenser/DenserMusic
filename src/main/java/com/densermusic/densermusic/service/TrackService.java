package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.DeezerTrackDTO;
import com.densermusic.densermusic.dto.DeezerTrackSearchResponseDTO;
import com.densermusic.densermusic.dto.DeezerTrackSearchResultDTO;
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

    private Optional<Track> fetchTrackFromDeezerApi(Long trackDeezerId, Long artistDeezerId) {

        DeezerTrackDTO trackDetailsDto = deezerClient.getTrackById(trackDeezerId);

        Artist artist = artistService.findOrCreateArtist(artistDeezerId);

        return Optional.of(Track.of(trackDetailsDto, artist));
    }

    public Track findOrCreateTrack(DeezerTrackSearchResultDTO trackSearchResult) {

        Long artistDeezerId = trackSearchResult.artist().deezerId();// variavel com o id do artista da track

        Long trackDeezerId = trackSearchResult.deezerId(); // variavel com id da track

        return trackRepository.findByDeezerId(trackDeezerId)
                .orElseGet(() -> {

                    // nao salvo, procura na api
                    Optional<Track> trackOptional = fetchTrackFromDeezerApi(trackDeezerId, artistDeezerId );

                    return trackOptional.map(this::save)
                            .orElseThrow(() -> new BusinessException("Não foi possível encontrar a "
                                    + "música no Deezer com o ID: " + trackDeezerId));
                });
    }

    // RETORNA LISTA DE TODAS TRACKS COM NOME CORRESPONDENTE
    public List<DeezerTrackSearchResultDTO> searchTracksByName(String trackName) {
        DeezerTrackSearchResponseDTO response = deezerClient.getTrackByName(trackName);
        if (response != null && response.data() != null) {
            return response.data();
        }
        return Collections.emptyList();
    }

    public Optional<Track> findTrackbyDbId(Long id) {
        return trackRepository.findById(id);
    }

    public Optional<Track> findTrackByDeezerId (Long deezerId) {
        return trackRepository.findByDeezerId(deezerId);
    }

    public List<Track> carregarTracksSalvas() {
        return trackRepository.findAll();
    }

}

