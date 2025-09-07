package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.DeezerTrack;
import com.densermusic.densermusic.dto.DeezerTrackSearchResponse;
import com.densermusic.densermusic.dto.DeezerTrackSearchResult;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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



    //SALVA TRACK NO BANCO (E ARTISTA SE NECESSARIO)
    public Track buscaESalvaTrack(DeezerTrackSearchResult trackEscolhida) {

        Long trackArtistDeezerId = trackEscolhida.artist().id();// variavel com o id da track

        Artist artist = artistService.findOrCreateArtist(trackArtistDeezerId);

        //agora com o artista salvo, podemos salvar a track

        Optional<Track> trackOptional = trackRepository.findByDeezerId(trackEscolhida.id());
        if (trackOptional.isPresent()) {
            throw new IllegalArgumentException("A musica '" + trackEscolhida.title() + "' já está salvo na sua biblioteca."); // encerra o método
        }

        DeezerTrack trackDetailsDto = deezerClient.getTrackById(trackEscolhida.id());
        Track newTrack = new Track(); // instancia a nova track

        //set para todos atributos da track
        newTrack.setName(trackDetailsDto.title());
        newTrack.setArtist(artist);
        newTrack.setDeezerId(trackDetailsDto.id());
        newTrack.setDurationInSeconds(trackDetailsDto.duration());
        newTrack.setAlbum(trackDetailsDto.album().title());
        newTrack.setRank(trackDetailsDto.rank());
        newTrack.setReleaseDate(LocalDate.parse(trackDetailsDto.releaseDate()));
        trackRepository.save(newTrack); // salva no banco

        System.out.println("Música '" + newTrack.getName() + "' salva com sucesso!");

        return newTrack;
    }

    // RETORNA LISTA DE TODAS TRACKS COM NOME CORRESPONDENTE
    public List<DeezerTrackSearchResult> buscaTracksPorNome(String trackName) {
        DeezerTrackSearchResponse response = deezerClient.getTrackByName(trackName);
        if (response != null && !response.data().isEmpty()) {
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

