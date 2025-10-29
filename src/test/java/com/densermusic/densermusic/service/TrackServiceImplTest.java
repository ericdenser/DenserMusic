package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.dto.trackDTO.*;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackServiceImplTest {

    @Captor
    private ArgumentCaptor<Track> trackArgumentCaptor;

    @Mock
    private DeezerClient deezerClient;

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private TrackServiceImpl trackService;

    @Test
    void findOrCreateTrack_shouldCreateNewTrack_whenTrackIsNotInDb() {

        // Arrange
        Long trackApiId = 27L;
        Long artistApiId = 10L;
        CreateTrackRequestDTO requestTrack = new CreateTrackRequestDTO(trackApiId, artistApiId);

        DeezerAlbumDTO albumDTO = new DeezerAlbumDTO("Ten");
        DeezerArtistDTO artistDTO = new DeezerArtistDTO("Pearl Jam", "pc_url", artistApiId, 2000);
        DeezerTrackDTO trackDTO = new DeezerTrackDTO("Jeremy", 297, 3, trackApiId, "1991-08-27", albumDTO,  artistDTO);

        Artist artist = Artist.of(artistDTO);

        Track newTrack = Track.of(trackDTO, artist);

        CreationResultDTO<Artist> resultArtist = new CreationResultDTO<>(artist, true);

        when(trackRepository.findByApiId(requestTrack.trackApiId()))
                .thenReturn(Optional.empty());

        when(deezerClient.getTrackById(requestTrack.trackApiId()))
                .thenReturn(trackDTO);

        when(artistService.findOrCreateArtist(any(CreateArtistRequestDTO.class)))
                .thenReturn(resultArtist);

        when(trackRepository.save(any(Track.class)))
                .thenReturn(newTrack);

        // Act
        CreationResultDTO<Track> result = trackService.findOrCreateTrack(requestTrack);

        //Assert

        assertTrue(result.created()); //true para track criada

        //deu save 1 vez
        verify(trackRepository, times(1)).save(trackArgumentCaptor.capture());

        // chamou o findOrCreate 1 vez
        verify(artistService, times(1)).findOrCreateArtist(any(CreateArtistRequestDTO.class));

        Track capturedTrack = trackArgumentCaptor.getValue();

        assertNotNull(capturedTrack); //track salva nao é nula
        assertEquals("Jeremy", capturedTrack.getName()); // nome correto
        assertEquals(27L, capturedTrack.getApiId()); // id correto
        assertEquals("Pearl Jam", capturedTrack.getArtist().getName()); // nome artista correto
        assertEquals(albumDTO.title(), capturedTrack.getAlbum()); // nome album correto


    }

    @Test
    void findOrCreateTrack_shouldReturnExistingTrack_whenTrackIsSaved() {

        // Arrange
        Long trackApiId = 27L;
        Long artistApiId = 10L;

        CreateTrackRequestDTO request = new CreateTrackRequestDTO(trackApiId, artistApiId);


        Artist existingArtist = new Artist("Pearl Jam", "pc_url", 2000, artistApiId);
        Track existingTrack = new Track(); // usando setters para clareza
        existingTrack.setId(1L); // id do db
        existingTrack.setApiId(trackApiId);
        existingTrack.setName("Jeremy");
        existingTrack.setArtist(existingArtist);

        when(trackRepository.findByApiId(trackApiId))
                .thenReturn(Optional.of(existingTrack));

        // Act
        CreationResultDTO<Track> result = trackService.findOrCreateTrack(request);

        // Assert

        assertNotNull(result); // nao nulo
        assertFalse(result.created());
        assertEquals("Jeremy", result.entity().getName());
        assertEquals(1L, result.entity().getId());

        verify(deezerClient, never()).getTrackById(trackApiId);
        verify(artistService, never()).findOrCreateArtist(any(CreateArtistRequestDTO.class));
        verify(trackRepository, never()).save(any(Track.class));
    }

    @Test
    void findOrCreateTrack_shouldThrowException_whenArtistIdDoesNotMatchTrackArtist() {

        //arrange
        Long trackApiId = 27L;
        Long wrongArtistApiId = 99L; // ID incorreto enviado
        Long correctArtistApiId = 10L;

        CreateTrackRequestDTO request = new CreateTrackRequestDTO(trackApiId, wrongArtistApiId);

        DeezerArtistDTO correctArtist = new DeezerArtistDTO("Pearl Jam", "pic", correctArtistApiId, 10000);
        DeezerTrackDTO trackDTO = new DeezerTrackDTO("Jeremy", 297, 3, trackApiId, "1991-08-27", new DeezerAlbumDTO("Ten"), correctArtist);

        when(trackRepository.findByApiId(trackApiId))
                .thenReturn(Optional.empty());

        when(deezerClient.getTrackById(trackApiId))
                .thenReturn(trackDTO);

        //act
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            trackService.findOrCreateTrack(request);
        });

        //assert
        assertTrue(exception.getMessage().contains("Conflito de dados"));
        verify(artistService, never()).findOrCreateArtist(any());
        verify(trackRepository, never()).save(any());
    }

    @Test
    void findOrCreateTrack_shouldCreateTrackAndArtist_whenNeitherExists() {
        // Arrange
        Long trackApiId = 27L;
        Long artistApiId = 10L;
        CreateTrackRequestDTO request = new CreateTrackRequestDTO(trackApiId, artistApiId);

        DeezerAlbumDTO albumDTO = new DeezerAlbumDTO("Ten");
        DeezerArtistDTO artistDTO = new DeezerArtistDTO("Pearl Jam", "pic_url", artistApiId, 10000);
        DeezerTrackDTO trackDTO = new DeezerTrackDTO("Jeremy", 297, 3, trackApiId, "1991-08-27", albumDTO, artistDTO);

        Artist createdArtist = Artist.of(artistDTO);
        Track createdTrack = Track.of(trackDTO, createdArtist);

        when(trackRepository.findByApiId(trackApiId))
                .thenReturn(Optional.empty());

        when(deezerClient.getTrackById(trackApiId))
                .thenReturn(trackDTO);

        when(artistService.findOrCreateArtist(any(CreateArtistRequestDTO.class)))
                .thenReturn(new CreationResultDTO<>(createdArtist, true));

        when(trackRepository.save(any(Track.class)))
                .thenReturn(createdTrack);

        // Act
        CreationResultDTO<Track> result = trackService.findOrCreateTrack(request);

        // Assert
        assertNotNull(result);
        assertTrue(result.created());
        assertEquals("Jeremy", result.entity().getName());
        assertEquals("Pearl Jam", result.entity().getArtist().getName());

        verify(trackRepository).save(any(Track.class));
        verify(artistService).findOrCreateArtist(new CreateArtistRequestDTO(artistApiId));
    }

    @Test
    void searchTracksByName_shouldReturnTrackList_whenSearchIsSuccessful() {

        // Arrange
        String trackName = "Jere";

        Long trackApiId1 = 27L;
        Long trackApiId2 = 28L;
        Long artistApiId = 10L;
        DeezerArtistDTO artistDTO = new DeezerArtistDTO("Pearl Jam", "pc_url", artistApiId, 2000);
        DeezerTrackSearchResultDTO trackResult1 = new DeezerTrackSearchResultDTO(trackApiId1, "Jeremy", artistDTO);
        DeezerTrackSearchResultDTO trackResult2 = new DeezerTrackSearchResultDTO(trackApiId2, "Jeremy2", artistDTO);

        DeezerTrackSearchResponseDTO mockResponse = new DeezerTrackSearchResponseDTO(List.of(trackResult1, trackResult2));

        when(deezerClient.getTrackByName(trackName))
                .thenReturn(mockResponse);

        // Act
        List<DeezerTrackSearchResultDTO> result = trackService.searchTracksByName(trackName);

        // Assert

        assertNotNull(result); // lista nao pode ser nula
        assertFalse(result.isEmpty()); // lista nao pode estar vazia
        assertEquals(2, result.size()); // 2 objetos na lista
        assertEquals("Jeremy", result.getFirst().title());

    }

    @Test
    void searchTracksByName_shouldReturnEmptyList_whenNoResultsFound() {
        // Arrange
        String name = "TrackInexistente777";
        DeezerTrackSearchResponseDTO response = new DeezerTrackSearchResponseDTO(Collections.emptyList());

        when(deezerClient.getTrackByName(name))
                .thenReturn(response);

        // Act
        List<DeezerTrackSearchResultDTO> result = trackService.searchTracksByName(name);

        // Assert

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void save_shouldPersistTrack_whenTrackDoesNotExist() {
        //arrange
        Track track = new Track();
        track.setApiId(123L);

        when(trackRepository.findByApiId(123L))
                .thenReturn(Optional.empty());

        when(trackRepository.save(track))
                .thenReturn(track);

        //act
        Track result = trackService.save(track);

        //assert
        assertNotNull(result);
        verify(trackRepository).save(track);
    }

    @Test
    void save_shouldThrowException_whenTrackAlreadyExists() {
        //arrange
        Track track = new Track();
        track.setApiId(123L);

        when(trackRepository.findByApiId(123L))
                .thenReturn(Optional.of(track));

        //act
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            trackService.save(track);
        });

        //assert
        assertEquals("Música com Deezer ID 123 já existe.", exception.getMessage());
        verify(trackRepository, never()).save(any());
    }

    @Test
    void deleteTrackByDbId_shouldDelete_whenTrackExists() {
        Long id = 1L;
        when(trackRepository.existsById(id)).thenReturn(true);

        trackService.deleteTrackByDbId(id);

        verify(trackRepository).deleteById(id);
    }

    @Test
    void deleteTrackByDbId_shouldThrowException_whenTrackDoesNotExist() {
        Long id = 1L;
        when(trackRepository.existsById(id)).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            trackService.deleteTrackByDbId(id);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(trackRepository, never()).deleteById(any());
    }

    @Test
    void findTrackByDbId_shouldReturnTrack_whenExists() {
        //arrange
        Long id = 1L;
        Track track = new Track();
        when(trackRepository.findById(id)).thenReturn(Optional.of(track));

        //act
        Optional<Track> result = trackService.findTrackByDbId(id);

        //assert
        assertTrue(result.isPresent());
    }

    @Test
    void findTrackByApiId_shouldReturnTrack_whenExists() {
        Long apiId = 123L;
        Track track = new Track();
        when(trackRepository.findByApiId(apiId)).thenReturn(Optional.of(track));

        //act
        Optional<Track> result = trackService.findTrackByApiId(apiId);

        assertTrue(result.isPresent());
    }

    @Test
    void loadSavedTracks_shouldReturnTrackList() {
        List<Track> tracks = List.of(new Track(), new Track());
        when(trackRepository.findAll()).thenReturn(tracks);

        //act
        List<Track> result = trackService.carregarTracksSalvas();

        assertEquals(2, result.size());
    }

}