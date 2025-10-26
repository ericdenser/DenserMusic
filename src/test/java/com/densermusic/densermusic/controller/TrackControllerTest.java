package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.trackDTO.*;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.mapper.TrackMapper;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.service.TrackService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrackController.class)
class TrackControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private TrackService trackService;

    @MockBean
    private TrackMapper trackMapper;



    @Test
    void loadSavedTracks_shouldReturn200WithList() throws Exception {

        List<Track> tracks = List.of(new Track(), new Track());

        ArtistResponseDTO artistDto1 = new ArtistResponseDTO(1L, 1L, "Nome1", "url1", 100);
        ArtistResponseDTO artistDto2 = new ArtistResponseDTO(2L, 2L, "Nome2", "url2", 200);

        TrackResponseDTO trackDto1 = new TrackResponseDTO(10L, 100L, "Jeremy", "Ten", 99, 90, LocalDate.parse("1991-08-27"), artistDto1);
        TrackResponseDTO trackDto2 = new TrackResponseDTO(11L, 101L, "Black", "Ten", 99, 90, LocalDate.parse("1991-08-28"), artistDto2);
        List<TrackResponseDTO> tracksDto = List.of(trackDto1, trackDto2);

        when(trackService.carregarTracksSalvas()).thenReturn(tracks);
        when(trackMapper.toDTOList(tracks)).thenReturn(tracksDto);

        mockMvc.perform(get("/api/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Jeremy"))
                .andExpect(jsonPath("$[1].name").value("Black"));
    }

    @Test
    void loadSavedTracks_shouldReturnEmptyList() throws Exception {

        when(trackService.carregarTracksSalvas()).thenReturn(List.of());
        when(trackMapper.toDTOList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void searchTracksByName_shouldReturn200WithList() throws Exception {
        DeezerArtistDTO artist = new DeezerArtistDTO("Pearl jam", "url", 200L, 100);
        DeezerTrackSearchResultDTO track = new DeezerTrackSearchResultDTO(100L, "Jeremy", artist);

        when(trackService.searchTracksByName("Pearl")).thenReturn(List.of(track));

        mockMvc.perform(get("/api/tracks/search")
                .param("name", "Pearl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Jeremy"));

    }

    @Test
    void searchTracksByName_shouldReturnEmptyList() throws Exception {

        when(trackService.searchTracksByName("Inexistente")).thenReturn(List.of());

        mockMvc.perform(get("/api/tracks/search")
                        .param("name", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void searchTracksByName_shouldReturn400_whenInvalidName() throws Exception {

        mockMvc.perform(get("/api/tracks/search").param("name", ""))
                .andExpect(status().isBadRequest());
    }


    @Test
    void getTrackById_shouldReturn200AndTrack_whenTrackIsSaved() throws Exception{

        ArtistResponseDTO artistDto = new ArtistResponseDTO(1L, 1L, "Pearl Jam", "url1", 100);

        TrackResponseDTO trackResponseDTO = new TrackResponseDTO(102L, 100L, "Jeremy", "Ten", 99, 90, LocalDate.parse("1991-08-27"), artistDto);

        when(trackService.findTrackByDbId(102L)).thenReturn(Optional.of(new Track()));
        when(trackMapper.toDTO(any())).thenReturn(trackResponseDTO);

        mockMvc.perform(get("/api/tracks/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jeremy"));
    }

    @Test
    void getTrackById_shouldReturn404_whenTrackNotFound() throws Exception{
        when(trackService.findTrackByDbId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tracks/333"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTrack_shouldReturn201_whenTrackIsCreated() throws Exception {

        CreateTrackRequestDTO request = new CreateTrackRequestDTO(100L, 101L);

        Artist existingArtist = new Artist("Pearl Jam", "pc_url", 2000, 101L);
        Track existingTrack = new Track();
        existingTrack.setId(1L); // id do db
        existingTrack.setApiId(100L);
        existingTrack.setName("Jeremy");
        existingTrack.setArtist(existingArtist);

        ArtistResponseDTO artistDto = new ArtistResponseDTO(2L, 101L, "Pearl Jam", "url1", 100);

        TrackResponseDTO trackResponseDTO = new TrackResponseDTO(1L, 100L, "Jeremy", "Ten", 99, 90, LocalDate.parse("1991-08-27"), artistDto);

        CreationResultDTO<Track> result = new CreationResultDTO<>(existingTrack, true);

        when(trackService.findOrCreateTrack(request)).thenReturn(result);
        when(trackMapper.toDTO(existingTrack)).thenReturn(trackResponseDTO);

        mockMvc.perform(post("/api/tracks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTrack_shouldReturn200_whenTrackAlreadyExist() throws Exception {

        ArtistResponseDTO artistDto = new ArtistResponseDTO(2L, 101L, "Pearl Jam", "url1", 100);

        TrackResponseDTO trackResponseDTO = new TrackResponseDTO(1L, 100L, "Jeremy", "Ten", 99, 90, LocalDate.parse("1991-08-27"), artistDto);

        CreateTrackRequestDTO request = new CreateTrackRequestDTO(100L, 101L);

        CreationResultDTO<Track> result = new CreationResultDTO<>(new Track(), false);

        when(trackService.findOrCreateTrack(request)).thenReturn(result);
        when(trackMapper.toDTO(any(Track.class))).thenReturn(trackResponseDTO);

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createTrack_shouldReturn400_whenInvalidRequest() throws Exception {
        CreateTrackRequestDTO request = new CreateTrackRequestDTO(null,  10L);

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]").value("O campo trackApiId é obrigatório."));
    }

    @Test
    void createTrack_shouldThrowException_whenArtistIdDoesNotMatchTrackArtist() throws Exception {
        Long trackApiId = 27L;
        Long wrongArtistApiId = 99L; // ID incorreto enviado
        CreateTrackRequestDTO request = new CreateTrackRequestDTO(trackApiId, wrongArtistApiId);

        // simula a exceção lançada pelo service
        when(trackService.findOrCreateTrack(any(CreateTrackRequestDTO.class)))
                .thenThrow(new BusinessException(
                        "Conflito de dados: A música com API_ID 27 pertence ao artista com API_ID 10, não ao artista com API_ID 99."));

        mockMvc.perform(post("/api/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Conflito de dados: A música com API_ID 27 pertence ao artista com API_ID 10, não ao artista com API_ID 99."));
    }

    @Test
    void deleteTrack_shouldReturn204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/tracks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTrack_shouldReturn422_whenBusinessException() throws Exception {
        Long id = 1L;
        doThrow(new BusinessException("Track com ID " + id + " não encontrado, não foi possível deletar."))
                .when(trackService).deleteTrackByDbId(1L);

        mockMvc.perform(delete("/api/tracks/1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message").value("Track com ID 1 não encontrado, não foi possível deletar."))
                .andExpect(jsonPath("$.status").value(422));

    }
}