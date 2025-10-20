package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerAlbumDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.PlaylistRepository;
import com.densermusic.densermusic.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceImplTest {

    @Captor
    private ArgumentCaptor<Playlist> playlistArgumentCaptor;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private TrackService trackService;

    @Mock
    private  TrackRepository trackRepository;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    @Test
    void createPlaylist_shouldCreatePlaylist_whenPlaylistDoesntExist() {
        // Arrange
        String name = "MinhaPlaylist";
        Playlist newPlaylist = new Playlist();
        newPlaylist.setName(name);

        when (playlistRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.empty());
        when (playlistRepository.save(any(Playlist.class)))
                .thenReturn(newPlaylist);

        // Act
        Playlist createdPlaylist = playlistService.createPlaylist(name);

        // Assert
        assertNotNull(createdPlaylist);
        assertEquals("MinhaPlaylist", createdPlaylist.getName());
        verify(playlistRepository, times(1)).save(any(Playlist.class));
    }

    @Test
    void createPlaylist_shouldReturnExistingPlaylist_whenPlaylistExists() {
        String name = "MinhaPlaylistQueJaExiste";
        Playlist playlist = new Playlist();
        playlist.setName(name);

        when (playlistRepository.findByNameIgnoreCase(name))
                .thenReturn(Optional.of(playlist));

        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            playlistService.createPlaylist(name);
        });

        // Assert
        String expectedMessage = "Ja existe uma playlist salva com o nome";
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(playlistRepository, never()).save(any(Playlist.class));
    }

    @Test
    void addTrackInPlaylist_shouldAddTrack_whenTrackIsNotInPlaylist() {
        // Arrange
        Long playlistId = 3L;
        Long trackId = 100L;

        Playlist testPlaylist = new Playlist();
        testPlaylist.setName("Minhas Favoritas");
        testPlaylist.setId(playlistId);

        Artist artist = new Artist("Pearl Jam", "pc_url", 2000, 10L);
        Track newTrack = new Track();
        newTrack.setId(trackId);
        newTrack.setName("Jeremy");
        newTrack.setArtist(artist);

        when(playlistRepository.findById(playlistId))
                .thenReturn(Optional.of(testPlaylist));

        when(trackService.findTrackByDbId(trackId))
                .thenReturn(Optional.of(newTrack));


        // Act
        playlistService.addTrackInPlaylist(playlistId, trackId);

        // Assert
        verify(playlistRepository, times(1)).save(playlistArgumentCaptor.capture());
        Playlist playlistFinal = playlistArgumentCaptor.getValue();

        assertNotNull(playlistFinal);
        assertEquals(testPlaylist.getId(), playlistFinal.getId());
        assertFalse(playlistFinal.getTracksOfPlaylist().isEmpty());
        assertEquals(1, playlistFinal.getTracksOfPlaylist().size());

    }

    @Test
    void addTrackInPlaylist_shouldNotAddTrack_whenTrackIsInPlaylist() {
        Long trackId = 11L;
        Long playlistId = 10L;

        Playlist playlist = new Playlist();
        playlist.setName("Minhas Favoritas");

        Artist artist = new Artist("Pearl Jam", "pc_url", 2000, 10L);
        Track newTrack = new Track();
        newTrack.setId(trackId);
        newTrack.setName("Jeremy");
        newTrack.setArtist(artist);
        playlist.getTracksOfPlaylist().add(newTrack);

        when(playlistRepository.findById(playlistId))
                .thenReturn(Optional.of(playlist));
        when(trackService.findTrackByDbId(trackId))
                .thenReturn(Optional.of(newTrack));

        // Act
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            playlistService.addTrackInPlaylist(playlistId, trackId);
        });

        String expectedMessage = "já está na playlist";
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(playlistRepository, never()).save(any(Playlist.class));

    }

    @Test
    void removeTrackFromPlaylist() {
    }

}