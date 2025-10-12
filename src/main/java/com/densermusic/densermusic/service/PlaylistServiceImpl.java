package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.PlaylistRepository;
import com.densermusic.densermusic.repository.TrackRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistServiceImpl.class);
    private final PlaylistRepository playlistRepository;
    private final TrackServiceImpl trackServiceImpl;
    private final TrackRepository trackRepository;

    public PlaylistServiceImpl(PlaylistRepository playlistRepository, TrackServiceImpl trackServiceImpl, TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.trackServiceImpl = trackServiceImpl;
        this.trackRepository = trackRepository;
    }

    //CRIA PLAYLIST
    @Transactional
    @Override
    public Playlist criarPlaylist(String nome) {
        if (nome == null || nome.isBlank()) {
            logger.warn("Nome informado para playlist está vazio: '{}'", nome);
            throw new IllegalArgumentException("Nome nao pode ser vazio");
        }

        if (playlistRepository.findByNameIgnoreCase(nome).isPresent()) {
            logger.warn("Nome '{}'ja é usado para outra playlist", nome);
            throw new BusinessException("Ja existe uma playlist salva com o nome: " + nome);
        }

        Playlist novaPlaylist = new Playlist();
        novaPlaylist.setName(nome);

        logger.info("Playlist com nome '{}' criada e salva", nome);
        return playlistRepository.save(novaPlaylist);

    }

    @Transactional
    @Override
    public void deletarPlaylist(Long playlistId) {

        if (!playlistRepository.existsById(playlistId)) {
            logger.warn("Tentativa de deletar playlist com ID de banco {} falhou. Playlist não encontrada.", playlistId);
            throw new BusinessException("Não foi possível deletar: Playlist com ID " + playlistId + " não encontrada.");
        }

        playlistRepository.deleteById(playlistId);
        logger.info("Playlist de ID {} foi deletada!", playlistId);
    }

    @Transactional
    @Override
    public Playlist updatePlaylistName(Long playlistId, String nome) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new BusinessException("Playlist com ID " + playlistId + " não encontrada."));

        playlist.setName(nome);

        logger.info("Nome da Playlist de ID {} foi atualizado para '{}' !", playlistId, nome);
        return playlist; //.save(playlist);  AGORA COM TRANSACTIONAL O OBJETO É SALVO NO DB AUTOMATICAMENTE
    }

    //ADICIONA UMA TRACK JA EXISTENTE A UMA PLAYLIST JA EXISTENTE
    @Transactional
    @Override
    public Playlist adicionarTrackNaPlaylist(Long playlistId, Long trackId) {
        //buscando do banco de dados
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist com ID {} nao encontrada no Banco de Dados.", playlistId);
                    return new BusinessException("Playlist com ID " + playlistId + " não encontrada.");
                });

        Track track = trackServiceImpl.findTrackByDbId(trackId)
                .orElseThrow(() -> {
                    logger.warn("Track com ID {} nao encontrada no Banco de Dados.", playlistId);
                    return new BusinessException("Track com ID: " + trackId + " não encontrada.\"");
                });

        // se a track ja esta na playlist
        if (playlist.getTracksOfPlaylist().contains(track)) {
            logger.warn("Track '{}' (ID {}) ja está salva nesta playlist (ID {}).", track.getName(), trackId, playlist);
            throw new BusinessException("A track '" + track.getName() + "' já está na playlist.");
        }

        playlist.getTracksOfPlaylist().add(track); //adiciona a track na lista
        logger.info("Track '{}' (ID {}) salva com sucesso playlist de ID {}.", track.getName(), trackId, playlistId);
        return playlist; //TRANSACTIONAL atualiza as alteracoes no banco
    }

    //REMOVE UMA TRACK JA EXISTENTE DE UMA PLAYLIST JA EXISTENTE
    @Transactional
    @Override
    public Playlist removerTrackDaPlaylist(Long playlistId, Long trackId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> {
                    logger.warn("Playlist com ID {} nao encontrada no Banco de Dados.", playlistId);
                    return new BusinessException("Playlist com ID " + playlistId + " não encontrada.");
                });

        Track track = trackServiceImpl.findTrackByDbId(trackId)
                .orElseThrow(() -> {
                    logger.warn("Track com ID {} nao encontrada no Banco de Dados.", trackId);
                    return new BusinessException("Track com ID: " + trackId + " não encontrada.\"");
                });

        if (!playlist.getTracksOfPlaylist().contains(track)) {
            logger.warn("Track '{}' (ID {}) não está salva nesta playlist (ID {}).", track.getName(), trackId, playlist);
            throw new BusinessException("A música '" + track.getName() + "' não está na playlist '" + playlist.getName() + "'.");
        }

        playlist.getTracksOfPlaylist().remove(track); //remove da lista
        logger.info("Track '{}' (ID {}) removida com sucesso da playlist de ID {}.", track.getName(), trackId, playlistId);
        return playlist; //TRANSACTIONAL atualiza as alteracoes no banco
    }

    @Override
    public List<Playlist> carregarPlaylistsSalvas() {
        return playlistRepository.findAll();
    }

    @Override
    public Optional<Playlist> buscarPlaylist(Long playlistId) {
        return playlistRepository.findById(playlistId);
    }

    @Override
    public List<Track> findAvailableTracksForPlaylist(Long playlistId) {
        if (!playlistRepository.existsById(playlistId)) {
            logger.warn("Playlist(ID: {}) não encontrada no Banco de Dados", playlistId);
            throw new BusinessException("Playlist com ID " + playlistId + " não encontrada.");
        }

        return trackRepository.findTracksNotInPlaylist(playlistId);
    }

    @Override
    public Optional<PlaylistDetailsDTO> findDetailsById(Long id) {
        return playlistRepository.findByIdWithTracks(id)
                .map(p -> new PlaylistDetailsDTO(p.getId(), p.getName(), p.getTracksOfPlaylist()));
    }
}
