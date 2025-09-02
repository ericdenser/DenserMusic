package com.DenserMusic.DenserMusic.service;

import com.DenserMusic.DenserMusic.client.DeezerClient;
import com.DenserMusic.DenserMusic.dto.*;
import com.DenserMusic.DenserMusic.model.Artist;
import com.DenserMusic.DenserMusic.model.Playlist;
import com.DenserMusic.DenserMusic.model.Track;
import com.DenserMusic.DenserMusic.repository.ArtistRepository;
import com.DenserMusic.DenserMusic.repository.PlaylistRepository;
import com.DenserMusic.DenserMusic.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ConsultaDeezerService {

    private final ArtistRepository artistRepository;
    private final PlaylistRepository playlistRepository;
    private final DeezerClient deezerClient;
    private final TrackRepository trackRepository;

    public ConsultaDeezerService(ArtistRepository artistRepository, PlaylistRepository playlistRepository, DeezerClient deezerClient, TrackRepository trackRepository) {
        this.artistRepository = artistRepository;
        this.playlistRepository = playlistRepository;
        this.deezerClient = deezerClient;
        this.trackRepository = trackRepository;
    }

    //SALVA UM ARTISTA NO BANCO (getFirst)
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

    //RETORNA LISTA DE TODOS OS ARTISTAS COM O NOME CORRESPONDENTE
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

    //SALVA TRACK NO BANCO (E ARTISTA SE NECESSARIO)
    public void buscaESalvaTrack(DeezerTrackSearchResult trackEscolhida) {
        String trackArtistName = trackEscolhida.artist().name(); // variavel com o nome da track
        Artist artistToSave; // instacia o artista
        Optional<Artist> artistOptional = artistRepository.findByNameIgnoreCase(trackArtistName); // procura se artista ja existe no banco

        if (artistOptional.isPresent()) { // se ja existe
            artistToSave = artistOptional.get(); // salva na variavel
            System.out.println("Artista '" + trackArtistName + "' já existe no banco de dados.");
        } else { // se nao existe
            System.out.println("Artista '" + trackArtistName + "' não encontrado no banco de dados, buscando na API Deezer...");
            Optional<Artist> newArtistOptional = buscaUmArtista(trackArtistName); // procura na API o artista
            if(newArtistOptional.isPresent()){
                artistToSave = artistRepository.save(newArtistOptional.get()); // salva artista no banco
                System.out.println("Novo artista '" + trackArtistName + "' salvo com sucesso!");
            } else {
                System.out.println("Não foi possível encontrar os detalhes do artista '" + trackArtistName + "' no Deezer.");
                return;
            }
        }

        //agora com o artista salvo, podemos salvar a track

        long deezerTrackId = trackEscolhida.id();

        if (trackRepository.existsByDeezerId(deezerTrackId)) {
            System.out.println("Esta música já está salva na sua biblioteca.");
            return; // encerra o método
        }

        DeezerTrack trackDetailsDto = deezerClient.getTrackById(trackEscolhida.id());
        Track newTrack = new Track(); // instancia a nova track

        //set para todos atributos da track
        newTrack.setName(trackDetailsDto.title());
        newTrack.setArtist(artistToSave);
        newTrack.setDeezerId(trackDetailsDto.deezerId());
        newTrack.setDurationInSeconds(trackDetailsDto.duration());
        newTrack.setAlbum(trackDetailsDto.album().title());
        newTrack.setRank(trackDetailsDto.rank());
        newTrack.setReleaseDate(LocalDate.parse(trackDetailsDto.releaseDate()));
        trackRepository.save(newTrack); // salva no banco

        System.out.println("Música '" + newTrack.getName() + "' salva com sucesso!");

    }

    // RETORNA LISTA DE TODAS TRACKS COM NOME CORRESPONDENTE
    public List<DeezerTrackSearchResult> buscaTracksPorNome(String trackName) {
        DeezerTrackSearchResponse response = deezerClient.getTrackByName(trackName);
        if (response != null && !response.data().isEmpty()) {
            return response.data();
        }
        return Collections.emptyList();
    }

    //CRIA PLAYLIST
    public void criarPlaylist(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome nao pode ser vazio");
        }

        Playlist novaPlaylist = new Playlist();
        novaPlaylist.setName(nome);
        Optional<Playlist> verificarDuplicata = playlistRepository.findByNameIgnoreCase(nome);

        if (verificarDuplicata.isEmpty()) {
            playlistRepository.save(novaPlaylist);
            System.out.println("Playlist " + nome + " criada com sucesso!");
        } else {
            System.out.println("Ja existe uma playlist salva com este nome");
        }

    }

    public void deletarPlaylist(Long playlistId) {

        if (playlistRepository.existsById(playlistId)) {
            playlistRepository.deleteById(playlistId);
            System.out.println("Playlist deletada com sucesso!");
        } else {
            System.out.println("Playlist nao encontrada");
        }
    }

    //ADICIONA UMA TRACK JA EXISTENTE A UMA PLAYLIST JA EXISTENTE
    public void adicionarTrackNaPlaylist(Long playlistId, Long trackId) {
        //instancia optionals que buscam do banco de dados
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        Optional<Track> trackOptional = trackRepository.findById(trackId);

        // desempacota se optionals existirem
        if (playlistOptional.isPresent() && trackOptional.isPresent()) {
            Playlist playlist = playlistOptional.get();
            Track track = trackOptional.get();

            playlist.getTracksOfPlaylist().add(track); //adiciona a track na lista
            playlistRepository.save(playlist); //salva as alteracoes no banco
            System.out.println("Música '" + track.getName() + "' adicionada à playlist '" + playlist.getName() + "'!");
        } else {
            System.out.println("Id invalido");
        }
    }

    //REMOVE UMA TRACK JA EXISTENTE DE UMA PLAYLIST JA EXISTENTE
    public void removerTrackDaPlaylist(long playlistId, long idTrackRemover) {
        //instancia optionals que buscam do banco de dados
        Optional<Playlist> playlistOptional = playlistRepository.findById(playlistId);
        Optional<Track> trackOptional = trackRepository.findById(idTrackRemover);

        // desempacota se optionals existirem
        if (playlistOptional.isPresent() && trackOptional.isPresent()) {
            Playlist playlist = playlistOptional.get();
            Track track = trackOptional.get();

            playlist.getTracksOfPlaylist().remove(track); //remove da lista
            playlistRepository.save(playlist); //salva alteracoes no banco
            System.out.println("Música '" + track.getName() + "' removida da playlist '" + playlist.getName() + "'!");
        } else {
            System.out.println("Id invalido");
        }
    }

    public List<Track> carregarTracksSalvas() {
        return trackRepository.findAll();
    }

    public List<Artist> carregarArtistasSalvos() {
        return artistRepository.findAll();
    }

    public List<Playlist> carregarPlaylistsSalvas() {
        return playlistRepository.findAll();
    }

}
