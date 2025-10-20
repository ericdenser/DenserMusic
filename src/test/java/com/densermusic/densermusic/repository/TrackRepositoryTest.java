package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TrackRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrackRepository trackRepository;

    private Playlist playlist1;


    @BeforeEach
    void setUpDataBase() {

        // cria e salva artista que vai ser utilizado para criar a musica
        Artist testArtist = new Artist("Pearl Jam", "pc_url", 2000, 11L);
        Artist savedArtist = entityManager.persist(testArtist);

        // cria e salva musica 1 (vai estar na playlist)
        Track trackInPlaylist = new Track();
        trackInPlaylist.setApiId(101L);
        trackInPlaylist.setName("NaPlaylist");
        trackInPlaylist.setArtist(savedArtist);
        entityManager.persist(trackInPlaylist);

        // cria e salva musica 2 (que nao vai estar na playlist)
        Track trackNotInPlaylist = new Track();
        trackNotInPlaylist.setApiId(102L);
        trackNotInPlaylist.setName("Track");
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
    void findByNameIgnoreCase_shouldReturnTrack_whenTrackExists() {

        //act
        Optional<Track> trackOptional = trackRepository.findByNameIgnoreCase("Track");

        //assert
        assertTrue(trackOptional.isPresent());
        assertEquals("Track", trackOptional.get().getName());
        assertEquals(102L, trackOptional.get().getApiId());
    }

    @Test
    void findByNameIgnoreCase_shouldReturnEmpty_whenTrackDoesNotExists() {

        //act
        Optional<Track> trackOptional = trackRepository.findByNameIgnoreCase("Errado");

        //assert
        assertTrue(trackOptional.isEmpty());
    }


    @Test
    void findByApiId_shouldReturnTrack_whenTrackExist() {
        //act
        Optional<Track> trackOptional = trackRepository.findByApiId(102L);

        //assert
        assertTrue(trackOptional.isPresent());
        assertEquals(102L, trackOptional.get().getApiId());
    }

    @Test
    void findByApiId_shouldReturnEmpty_whenTrackDoesNotExist() {
        //act
        Optional<Track> trackOptional = trackRepository.findByApiId(100L);

        //assert
        assertTrue(trackOptional.isEmpty());
    }

    @Test
    void findTracksNotInPlaylist_shouldReturnListOfTracksNotInTheSpecifiedPlaylist() {
        // act
        List<Track> tracksNotInPlaylist = trackRepository.findTracksNotInPlaylist(playlist1.getId());

        //assert
        assertFalse(tracksNotInPlaylist.isEmpty());
        assertEquals("Track", tracksNotInPlaylist.getFirst().getName());
        assertEquals(1, tracksNotInPlaylist.size());
    }
}