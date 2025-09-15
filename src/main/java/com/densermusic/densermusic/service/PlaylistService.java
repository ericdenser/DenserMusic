package com.densermusic.densermusic.service;

import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.PlaylistRepository;
import com.densermusic.densermusic.repository.TrackRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackService trackService;
    private final TrackRepository trackRepository;

    public PlaylistService(PlaylistRepository playlistRepository, TrackService trackService, TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.trackService = trackService;
        this.trackRepository = trackRepository;
    }

    //CRIA PLAYLIST
    public Playlist criarPlaylist(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome nao pode ser vazio");
        }

        if (playlistRepository.findByNameIgnoreCase(nome).isPresent()) {
            throw new BusinessException("Ja existe uma playlist salva com o nome: " + nome);
        }

        Playlist novaPlaylist = new Playlist();
        novaPlaylist.setName(nome);

        return playlistRepository.save(novaPlaylist);

    }

    public void deletarPlaylist(Long playlistId) {

        if (!playlistRepository.existsById(playlistId)) {
            throw new BusinessException("Não foi possível deletar: Playlist com ID " + playlistId + " não encontrada.");
        }

        playlistRepository.deleteById(playlistId);
    }

    //ADICIONA UMA TRACK JA EXISTENTE A UMA PLAYLIST JA EXISTENTE
    public Playlist adicionarTrackNaPlaylist(Long playlistId, Long trackId) {
        //instancia optionals que buscam do banco de dados
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException("Playlist com ID " + playlistId + " não encontrada."));
        Track track = trackService.findTrackbyDbId(trackId)
                .orElseThrow(() -> new BusinessException("Música com ID " + trackId + " não encontrada."));

        // desempacota se optionals existirem
        if (playlist.getTracksOfPlaylist().contains(track)) {
            throw new BusinessException("A música '" + track.getName() + "' já está na playlist.");
        }

        playlist.getTracksOfPlaylist().add(track); //adiciona a track na lista
        return playlistRepository.save(playlist); //salva as alteracoes no banco
    }

    //REMOVE UMA TRACK JA EXISTENTE DE UMA PLAYLIST JA EXISTENTE
    public Playlist removerTrackDaPlaylist(Long playlistId, Long idTrackRemover) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException("Playlist com ID " + playlistId + " não encontrada."));
        Track track = trackService.findTrackbyDbId(idTrackRemover)
                .orElseThrow(() -> new BusinessException("Música com ID " + idTrackRemover + " não encontrada."));

        if (!playlist.getTracksOfPlaylist().contains(track)) {
            throw new BusinessException("A música '" + track.getName() + "' não está na playlist '" + playlist.getName() + "'.");
        }

        playlist.getTracksOfPlaylist().remove(track); //remove da lista
        return playlistRepository.save(playlist); //salva alteracoes no banco
    }

    public List<Playlist> carregarPlaylistsSalvas() {
        return playlistRepository.findAll();
    }

    public Optional<Playlist> buscarPlaylist(Long playlistId) {
        return playlistRepository.findById(playlistId);
    }

    public List<Track> findAvailableTracksForPlaylist(Long playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            throw new BusinessException("Playlist com ID " + playlistId + " não encontrada.");
        }
        return trackRepository.findTracksNotInPlaylist(playlistId);
    }
}
