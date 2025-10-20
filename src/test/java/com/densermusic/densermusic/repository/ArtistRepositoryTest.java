package com.densermusic.densermusic.repository;

import com.densermusic.densermusic.model.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // prepara o ambiente
class ArtistRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // manipular as entidades com o banco

    @Autowired
    private ArtistRepository artistRepository; // interface que quer testar

    @Test
    void findByApiId_shouldReturnArtist_whenArtistExists() {
        // Arrange
        Artist newArtist = new Artist("Test Artist", "url", 1000, 12345L);

        entityManager.persistAndFlush(newArtist); // salva no banco h2

        // act
        Optional<Artist> foundArtistOptional = artistRepository.findByApiId(12345L);

        // assert
        assertTrue(foundArtistOptional.isPresent());
        assertEquals("Test Artist", foundArtistOptional.get().getName());
        assertEquals(12345L, foundArtistOptional.get().getApiId());
    }

    @Test
    void findByApiId_shouldReturnEmpty_whenArtistDoesNotExist() {

        // Act
        Optional<Artist> foundArtistOptional = artistRepository.findByApiId(999L);

        // Assert
        assertFalse(foundArtistOptional.isPresent());
    }

    @Test
    void findByNameIgnoreCase_shouldReturnArtist_whenArtistExist() {
        // Arrange
        Artist newArtist = new Artist("Test Artist", "url", 1000, 12345L);

        entityManager.persistAndFlush(newArtist);

        // Act
        Optional<Artist> foundArtistOptional = artistRepository.findByNameIgnoreCase("Test Artist");

        // Assert
        assertTrue(foundArtistOptional.isPresent());
    }

    @Test
    void findByNameIgnoreCase_shouldReturnEmpty_whenArtistNotFound() {
        //Arrange

        //Act
        Optional<Artist> artistOptional = artistRepository.findByNameIgnoreCase("Nome Errado");

        //Assert

        assertFalse(artistOptional.isPresent());

    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnArtist_whenArtistExists() {
        //Arrange
        Artist newArtist = new Artist("Test artist", "url", 1000, 129L);

        entityManager.persistAndFlush(newArtist);

        //Act
        Optional<List<Artist>> foundArtists = artistRepository.findByNameContainingIgnoreCase("Test");

        //Assert
        assertFalse(foundArtists.isEmpty());
        assertEquals(1, foundArtists.get().size());
        assertEquals("Test artist", foundArtists.get().getFirst().getName());
    }
}