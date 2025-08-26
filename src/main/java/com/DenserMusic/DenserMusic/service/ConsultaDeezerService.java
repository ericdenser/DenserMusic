package com.DenserMusic.DenserMusic.service;

import com.DenserMusic.DenserMusic.client.DeezerClient;
import com.DenserMusic.DenserMusic.dto.DeezerArtist;
import com.DenserMusic.DenserMusic.dto.DeezerResponse;
import com.DenserMusic.DenserMusic.model.Artista;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultaDeezerService {

    private final DeezerClient deezerClient;

    public ConsultaDeezerService(DeezerClient deezerClient) {
        this.deezerClient = deezerClient;
    }

    public Optional<Artista> buscaArtista(String nome) {
        DeezerResponse response = deezerClient.searchArtistByName(nome);

        if (response != null && !response.data().isEmpty()) {
            DeezerArtist deezerArtist = response.data().getFirst();

            Artista artista = new Artista(
                    deezerArtist.name(),
                    deezerArtist.picture(),
                    deezerArtist.totalFas()
            );

            return Optional.of(artista);
        }

        return Optional.empty();
    }

    public List<Artista> buscaArtistasPorNome(String nome) {
        DeezerResponse response = deezerClient.searchArtistByName(nome);

        if (response != null && !response.data().isEmpty()) {
            return response.data().stream()
                    .map(deezerArtist -> new Artista(
                            deezerArtist.name(),
                            deezerArtist.picture(),
                            deezerArtist.totalFas()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
