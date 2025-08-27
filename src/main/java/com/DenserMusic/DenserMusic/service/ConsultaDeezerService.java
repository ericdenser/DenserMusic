package com.DenserMusic.DenserMusic.service;

import com.DenserMusic.DenserMusic.client.DeezerClient;
import com.DenserMusic.DenserMusic.dto.*;
import com.DenserMusic.DenserMusic.model.Artist;
import com.DenserMusic.DenserMusic.model.Track;
import com.DenserMusic.DenserMusic.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaDeezerService {

    private final DeezerClient deezerClient;
    private final TrackRepository trackRepository;

    public ConsultaDeezerService(DeezerClient deezerClient, TrackRepository trackRepository) {
        this.deezerClient = deezerClient;
        this.trackRepository = trackRepository;
    }

    public Optional<Artist> buscaUmArtista(String nome) {
        DeezerArtistSearchResponse response = deezerClient.searchArtistByName(nome);

        if (response != null && !response.data().isEmpty()) {
            DeezerArtist deezerArtist = response.data().getFirst();

            Artist artist = new Artist(
                    deezerArtist.name(),
                    deezerArtist.picture(),
                    deezerArtist.totalFas()
            );

            return Optional.of(artist);
        }

        return Optional.empty();
    }

    public List<Artist> buscaArtistasPorNome(String nome) {
        DeezerArtistSearchResponse response = deezerClient.searchArtistByName(nome);

        if (response != null && !response.data().isEmpty()) {
            return response.data().stream()
                    .map(deezerArtist -> new Artist(
                            deezerArtist.name(),
                            deezerArtist.picture(),
                            deezerArtist.totalFas()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void salvarMusicaPorId(Long trackId, Artist artist) {
        DeezerTrack response = deezerClient.getTrackById(trackId);

        Track newTrack = new Track();

        newTrack.setName(response.title());
        newTrack.setArtist(artist);
        newTrack.setDurationInSeconds(response.duration());
        newTrack.setAlbum(response.album().title());
        newTrack.setRank(response.rank());

        newTrack.setReleaseDate(LocalDate.parse(response.releaseDate()));

        trackRepository.save(newTrack);

        System.out.println("Música '" + newTrack.getName() + "' salva com sucesso!");
    }

    public List<DeezerTrackSearchResult> buscaTracksPorNome(String trackName) {
        DeezerTrackSearchResponse response = deezerClient.searchTrackByName(trackName);
        if (response != null && !response.data().isEmpty()) {
            return response.data();
        }
        return Collections.emptyList();
    }
}
