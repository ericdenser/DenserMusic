package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.DeezerArtist;
import com.densermusic.densermusic.dto.DeezerArtistSearchResponse;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final DeezerClient deezerClient;

    public ArtistService(ArtistRepository artistRepository, DeezerClient deezerClient) {
        this.artistRepository = artistRepository;
        this.deezerClient = deezerClient;
    }

    //SALVA UM ARTISTA NO BANCO (getFirst)
    public Optional<Artist> buscaUmArtista(Long artistDeezerId) {
        DeezerArtist deezerArtistDto = deezerClient.searchArtistById(artistDeezerId);

        return Optional.ofNullable(deezerArtistDto)
                .map(dto -> new Artist(
                        dto.name(),
                        dto.picture(),
                        dto.totalFas(),
                        dto.id()
                ));
    }

    //RETORNA LISTA DE TODOS OS ARTISTAS COM O NOME CORRESPONDENTE
    public List<Artist> buscaArtistasPorNome(String nome) {
        DeezerArtistSearchResponse response = deezerClient.searchArtistByName(nome);

        if (response != null && !response.data().isEmpty()) {
            return response.data().stream()
                    .map(deezerArtist -> new Artist(
                            deezerArtist.name(),
                            deezerArtist.picture(),
                            deezerArtist.totalFas(),
                            deezerArtist.id()))
                    .collect(Collectors.toList());
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
        return artistRepository.save(artist);
    }

    public Artist findOrCreateArtist(Long artistDeezerId) {

        //procura se ja esta salvo
        Optional<Artist> dbArtistOptional = artistRepository.findByDeezerId(artistDeezerId);

        if (dbArtistOptional.isPresent()) {
            System.out.println("Ja salvo no banco!");
            return dbArtistOptional.get(); // ja temos, retorna.
        }

        //caso nao esteja salvo ainda
        Optional<Artist> artistOptional = buscaUmArtista(artistDeezerId);
        return artistOptional.map(artistRepository::save).orElseThrow(() ->// se o Optional estiver vazio, lança uma exceção.
                new IllegalArgumentException("Não foi possível encontrar o artista no Deezer com o ID: " + artistDeezerId)
        );
    }

    public List<Artist> carregarArtistasSalvos() {
        return artistRepository.findAll();
    }
}

