package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.playlistDTO.PlaylistDetailsDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.mapper.TrackMapper;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.PlaylistRepository;
import com.densermusic.densermusic.repository.TrackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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
    private TrackMapper trackMapper;

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
        Track track = new Track();
        track.setId(trackId);
        track.setName("Jeremy");
        track.setArtist(artist);

        when(playlistRepository.findById(playlistId))
                .thenReturn(Optional.of(testPlaylist));

        when(trackService.findTrackByDbId(trackId))
                .thenReturn(Optional.of(track));


        // Act
        playlistService.addTrackInPlaylist(playlistId, trackId);

        // Assert
        verify(playlistRepository).save(playlistArgumentCaptor.capture());
        Playlist captured = playlistArgumentCaptor.getValue();


        assertTrue(captured.getTracksOfPlaylist().contains(track));

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
        verify(playlistRepository, never()).save(any());

    }

    @Test
    void removeTrackFromPlaylist_shouldRemoveTrack_whenTrackIsInPlaylist() {
        // Arrange
        Long playlistId = 1L;
        Long trackId = 10L;

        Artist artist = new Artist("Pearl Jam", "pc_url", 2000, 100L);
        Track track = new Track();
        track.setId(trackId);
        track.setName("Jeremy");
        track.setArtist(artist);

        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setName("Rock Clássico");
        playlist.getTracksOfPlaylist().add(track); // adiciona na playlist

        when(playlistRepository.findById(playlistId))
                .thenReturn(Optional.of(playlist));

        when(trackService.findTrackByDbId(trackId))
                .thenReturn(Optional.of(track));

        when(playlistRepository.save(any(Playlist.class)))
                .thenReturn(playlist);

        // Act
        Playlist result = playlistService.removeTrackFromPlaylist(playlistId, trackId);

        // Assert
        assertNotNull(result);
        assertEquals(playlistId, result.getId());
        assertTrue(result.getTracksOfPlaylist().isEmpty()); // deve ter sido removida

        verify(playlistRepository, times(1)).save(playlistArgumentCaptor.capture());

    }

    @Test
    void removeTrackFromPlaylist_shouldThrowException_whenTrackIsNotInPlaylist() {
        // Arrange
        Long playlistId = 1L;
        Long trackId = 10L;

        Artist artist = new Artist("Pearl Jam", "pc_url", 2000, 100L);
        Track track = new Track();
        track.setId(trackId);
        track.setName("Jeremy");
        track.setArtist(artist);

        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setName("Rock Clássico");
        // track NÃO está na playlist

        when(playlistRepository.findById(playlistId))
                .thenReturn(Optional.of(playlist));

        when(trackService.findTrackByDbId(trackId))
                .thenReturn(Optional.of(track));

        // act + assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            playlistService.removeTrackFromPlaylist(playlistId, trackId);
        });

        String expectedMessage = "não está na playlist";
        assertTrue(exception.getMessage().contains(expectedMessage));
        verify(playlistRepository, never()).save(any());
    }

    @Test
    void updatePlaylistName_shouldUpdateName_whenPlaylistExists() {
        Long playlistId = 1L;
        String newName = "Nova Playlist";

        Playlist playlist = new Playlist();
        playlist.setId(playlistId);
        playlist.setName("Antigo Nome");

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));

        Playlist result = playlistService.updatePlaylistName(playlistId, newName);

        assertEquals(newName, result.getName());
    }

    @Test
    void updatePlaylistName_shouldThrow_whenPlaylistDoesNotExist() {
        when(playlistRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () ->
                playlistService.updatePlaylistName(1L, "Novo nome"));
    }

    @Test
    void deletePlaylist_shouldDelete_whenExists() {
        Long id = 5L;
        when(playlistRepository.existsById(id)).thenReturn(true);

        playlistService.deletePlaylist(id);

        verify(playlistRepository).deleteById(id);
    }

    @Test
    void deletePlaylist_shouldThrow_whenNotFound() {
        when(playlistRepository.existsById(1L)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () ->
                playlistService.deletePlaylist(1L));

        assertTrue(exception.getMessage().contains("não encontrada"));
        verify(playlistRepository, never()).deleteById(any());
    }

    @Test
    void findAvailableTracksForPlaylist_shouldReturnList_whenPlaylistExists() {
        Long playlistId = 1L;

        Track track1 = new Track();
        track1.setId(100L);
        Track track2 = new Track();
        track2.setId(101L);

        List<Track> mockList = List.of(track1, track2);

        when(playlistRepository.existsById(playlistId)).thenReturn(true);
        when(trackRepository.findTracksNotInPlaylist(playlistId)).thenReturn(mockList);

        List<Track> result = playlistService.findAvailableTracksForPlaylist(playlistId);

        assertEquals(2, result.size());
    }

    @Test
    void findAvailableTracksForPlaylist_shouldThrow_whenPlaylistNotFound() {
        Long playlistId = 999L;
        when(playlistRepository.existsById(playlistId)).thenReturn(false);

        assertThrows(BusinessException.class, () ->
                playlistService.findAvailableTracksForPlaylist(playlistId));
    }

    @Test
    void loadSavedPlaylists_shouldReturnAllPlaylists() {
        Playlist playlist1 = new Playlist();
        playlist1.setId(1L);
        playlist1.setName("P1");

        Playlist playlist2 = new Playlist();
        playlist2.setId(2L);
        playlist2.setName("P2");

        when(playlistRepository.findAll()).thenReturn(List.of(playlist1, playlist2));

        List<Playlist> result = playlistService.loadSavedPlaylists();

        assertEquals(2, result.size());
    }

    @Test
    void searchPlaylist_shouldReturnOptional_whenFound() {
        Playlist playlist = new Playlist();
        playlist.setId(10L);
        when(playlistRepository.findById(10L)).thenReturn(Optional.of(playlist));

        Optional<Playlist> result = playlistService.searchPlaylist(10L);
        assertTrue(result.isPresent());
    }

    @Test
    void findDetailsById_shouldReturnDTO_whenPlaylistFound() {
        Long id = 1L;
        Playlist playlist = new Playlist();
        playlist.setId(id);
        playlist.setName("Detalhada");
        playlist.setTracksOfPlaylist(new ArrayList<>());

        when(playlistRepository.findByIdWithTracks(id)).thenReturn(Optional.of(playlist));

        Optional<PlaylistDetailsDTO> result = playlistService.findDetailsById(id);

        assertTrue(result.isPresent());
        assertEquals("Detalhada", result.get().name());
    }

    @Test
    void findDetailsById_shouldReturnEmpty_whenNotFound() {
        when(playlistRepository.findByIdWithTracks(anyLong())).thenReturn(Optional.empty());

        Optional<PlaylistDetailsDTO> result = playlistService.findDetailsById(404L);
        assertTrue(result.isEmpty());
    }


}