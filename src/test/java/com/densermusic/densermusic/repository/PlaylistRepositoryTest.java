package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.mapper.ArtistMapper;
import com.densermusic.densermusic.mapper.TrackMapper;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PlaylistRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlaylistRepository playlistRepository;

    private Playlist playlist1;

    private TrackMapper trackMapper;

    @BeforeEach // antes de cada teste
    void setUpDataBase() {

        ArtistMapper artistMapper = new ArtistMapper();
        trackMapper = new TrackMapper(artistMapper);

        // cria e salva artista que vai ser utilizado para criar a musica
        Artist testArtist = new Artist("Pearl Jam", "pc_url", 2000, 11L);
        Artist savedArtist = entityManager.persist(testArtist);

        // cria e salva musica 1 (vai estar na playlist)
        Track trackInPlaylist = new Track();
        trackInPlaylist.setApiId(101L);
        trackInPlaylist.setName("Track na Playlist");
        trackInPlaylist.setArtist(savedArtist);
        entityManager.persist(trackInPlaylist);

        // cria e salva musica 2 (que nao vai estar na playlist)
        Track trackNotInPlaylist = new Track();
        trackNotInPlaylist.setApiId(102L);
        trackNotInPlaylist.setName("Track fora da Playlist");
        trackNotInPlaylist.setArtist(savedArtist);
        entityManager.persist(trackNotInPlaylist);

        // cria playlist e salva apenas 1 musica
        playlist1 = new Playlist();
        playlist1.setName("Rock");
        playlist1.getTracksOfPlaylist().add(trackInPlaylist);
        entityManager.persist(playlist1);

        entityManager.flush();
    }

    @Test
    void findByNameIgnoreCase_shouldReturnPlaylist_whenPlaylistExist() {

        // Act
        Optional<Playlist> foundPlaylistOptional = playlistRepository.findByNameIgnoreCase("Rock");

        // Assert
        assertTrue(foundPlaylistOptional.isPresent());
    }

    @Test
    void findByNameIgnoreCase_shouldReturnEmpty_whenPlaylistNotFound() {

        // Act
        Optional<Playlist> playlistOptional = playlistRepository.findByNameIgnoreCase("Nome errado");

        // Assert
        assertFalse(playlistOptional.isPresent());
    }


    @Test
    void findByIdWithTracks_shouldReturnPlaylistAndDetails_whenPlaylistExist () {

        // act
        Optional<PlaylistDetailsDTO> playlistOptional = playlistRepository.findByIdWithTracks(playlist1.getId())
                .map(p -> new PlaylistDetailsDTO(p.getId(), p.getName(), trackMapper.toDTOList(p.getTracksOfPlaylist())));

        //assert
        assertTrue(playlistOptional.isPresent());
        assertFalse(playlistOptional.get().tracks().isEmpty());
    }

}