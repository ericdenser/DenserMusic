package com.densermusic.densermusic.service;

import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.PlaylistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackService trackService;

    public PlaylistService(PlaylistRepository playlistRepository, TrackService trackService) {
        this.playlistRepository = playlistRepository;
        this.trackService = trackService;
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
        Optional<Track> trackOptional = trackService.findTrackbyDbId(trackId);

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
        Optional<Track> trackOptional = trackService.findTrackbyDbId(idTrackRemover);

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

    public List<Playlist> carregarPlaylistsSalvas() {
        return playlistRepository.findAll();
    }

    public Optional<Playlist> buscarPlaylist(Long playlistId) {
        return playlistRepository.findById(playlistId);
    }
}
