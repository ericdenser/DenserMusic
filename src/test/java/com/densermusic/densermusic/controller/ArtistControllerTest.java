package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.mapper.ArtistMapper;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.service.ArtistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ArtistController.class)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArtistService artistService;

    @MockBean
    private ArtistMapper artistMapper;


    @Test
    @DisplayName("GET /api/artists - Deve retornar 200 OK com lista de artistas")
    void loadSavedArtists_shouldReturn200WithList() throws Exception {
        Artist artist1 = new Artist("Nome1", "url1", 100, 1L);
        Artist artist2 = new Artist("Nome2", "url2", 200, 2L);

        ArtistResponseDTO dto1 = new ArtistResponseDTO(1L, 1L, "Nome1", "url1", 100);
        ArtistResponseDTO dto2 = new ArtistResponseDTO(2L, 2L, "Nome2", "url2", 200);

        when(artistService.loadSavedArtists()).thenReturn(List.of(artist1, artist2));
        when(artistMapper.toDTOList(List.of(artist1, artist2))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Nome1"))
                .andExpect(jsonPath("$[1].name").value("Nome2"));
    }

    @Test
    @DisplayName("GET /api/artists - Deve retornar lista vazia se não houver artistas")
    void loadSavedArtists_shouldReturnEmptyList() throws Exception {
        when(artistService.loadSavedArtists()).thenReturn(List.of());
        when(artistMapper.toDTOList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    // ------------------- GET /api/artists/search -------------------
    @Test
    @DisplayName("GET /api/artists/search - Deve retornar resultados da pesquisa")
    void searchArtistsByName_shouldReturnList() throws Exception {
        DeezerArtistDTO result = new DeezerArtistDTO("Artista", "url", 10L, 100);
        when(artistService.searchArtistsByName("Artista")).thenReturn(List.of(result));

        mockMvc.perform(get("/api/artists/search")
                        .param("name", "Artista"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Artista"));
    }

    @Test
    @DisplayName("GET /api/artists/search - Deve retornar lista vazia se não houver resultados")
    void searchArtistsByName_shouldReturnEmptyList() throws Exception {
        when(artistService.searchArtistsByName("Inexistente")).thenReturn(List.of());

        mockMvc.perform(get("/api/artists/search")
                        .param("name", "Inexistente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("GET /api/artists/search - nome inválido (null ou vazio)")
    void searchArtistByName_shouldReturn400_whenInvalidName() throws Exception {
        mockMvc.perform(get("/api/artists/search").param("name", ""))
                .andExpect(status().isBadRequest());
    }

    // ------------------- GET /api/artists/{id} -------------------
    @Test
    @DisplayName("GET /api/artists/{id} - Deve retornar 200 OK quando artista existe")
    void getArtistById_shouldReturn200_whenArtistIsSaved() throws Exception {
        Artist artist = new Artist("Nome Teste", "url", 100, 123L);
        ArtistResponseDTO artistDTO = new ArtistResponseDTO(1L, 123L, "Nome Teste", "url", 100);

        when(artistService.findByDbId(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        mockMvc.perform(get("/api/artists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nome Teste"));
    }

    @Test
    @DisplayName("GET /api/artists/{id} - Deve retornar 404 Not Found quando artista não existe")
    void getArtistById_shouldReturn404_whenArtistNotFound() throws Exception {
        when(artistService.findByDbId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/artists/999"))
                .andExpect(status().isNotFound());
    }

    // ------------------- POST /api/artists -------------------
    @Test
    @DisplayName("POST /api/artists - Deve retornar 201 Created quando novo artista é criado")
    void createArtist_shouldReturn201_whenArtistIsCreated() throws Exception {
        CreateArtistRequestDTO request = new CreateArtistRequestDTO(123L);
        Artist artist = new Artist("Nome Teste", "url", 100, 123L);
        ArtistResponseDTO responseDTO = new ArtistResponseDTO(1L, 123L, "Nome Teste", "url", 100);
        CreationResultDTO<Artist> serviceResult = new CreationResultDTO<>(artist, true);

        when(artistService.findOrCreateArtist(any(CreateArtistRequestDTO.class))).thenReturn(serviceResult);
        when(artistMapper.toDTO(artist)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/artists/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/artists - Deve retornar 200 OK quando artista já existe")
    void createArtist_shouldReturn200_whenArtistAlreadyExists() throws Exception {
        CreateArtistRequestDTO request = new CreateArtistRequestDTO(123L);
        Artist artist = new Artist("Nome Teste", "url", 100, 123L);
        ArtistResponseDTO responseDTO = new ArtistResponseDTO(1L, 123L, "Nome Teste", "url", 100);
        CreationResultDTO<Artist> serviceResult = new CreationResultDTO<>(artist, false);

        when(artistService.findOrCreateArtist(any(CreateArtistRequestDTO.class))).thenReturn(serviceResult);
        when(artistMapper.toDTO(artist)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("POST /api/artists - Deve retornar 400 Bad Request quando request inválido")
    void createArtist_shouldReturn400_whenInvalidRequest() throws Exception {
        CreateArtistRequestDTO request = new CreateArtistRequestDTO(null);

        mockMvc.perform(post("/api/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[0]").value("O campo apiId é obrigatório."));
    }

    @Test
    @DisplayName("DELETE /api/artists/{id} - Deve retornar 204 No Content quando sucesso")
    void deleteArtist_shouldReturn204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/artists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/artists/{id} - Deve retornar 422 quando service lança BusinessException")
    void deleteArtist_shouldReturn422_whenBusinessException() throws Exception {
        doThrow(new BusinessException("Artista não pode ser deletado"))
                .when(artistService).deleteArtistByDbId(1L);

        mockMvc.perform(delete("/api/artists/1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message").value("Artista não pode ser deletado"))
                .andExpect(jsonPath("$.status").value(422));

    }
}
