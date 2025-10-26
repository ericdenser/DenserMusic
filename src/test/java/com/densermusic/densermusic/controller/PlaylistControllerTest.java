package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.playlistDTO.*;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.mapper.PlaylistMapper;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.service.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaylistService playlistService;

    @MockBean
    private PlaylistMapper playlistMapper;

    @Test
    void getAllPlaylists_shouldReturn200WithList() throws Exception {
        Playlist playlist1 = new Playlist();
        playlist1.setName("Rock Classics");
        Playlist playlist2 = new Playlist();
        playlist2.setName("Pop Hits");

        PlaylistResponseDTO dto1 = new PlaylistResponseDTO(1L, "Rock Classics");
        PlaylistResponseDTO dto2 = new PlaylistResponseDTO(2L, "Pop Hits");

        when(playlistService.loadSavedPlaylists()).thenReturn(List.of(playlist1, playlist2));
        when(playlistMapper.toDTOList(List.of(playlist1, playlist2))).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Rock Classics"))
                .andExpect(jsonPath("$[1].name").value("Pop Hits"));
    }

    @Test
    void getAllPlaylists_shouldReturnEmptyList() throws Exception {
        when(playlistService.loadSavedPlaylists()).thenReturn(List.of());
        when(playlistMapper.toDTOList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    void getPlaylistById_shouldReturn200_whenExists() throws Exception {
        Playlist playlist = new Playlist();
        playlist.setName("Chill Vibes");
        PlaylistDetailsDTO dto = new PlaylistDetailsDTO(1L, "Chill Vibes", List.of());

        when(playlistService.findDetailsById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/playlists/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Chill Vibes"));
    }

    @Test
    void getPlaylistById_shouldReturn404_whenNotFound() throws Exception {
        when(playlistService.findDetailsById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/playlists/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPlaylist_shouldReturn201_whenCreated() throws Exception {
        CreatePlaylistRequestDTO request = new CreatePlaylistRequestDTO("Nova Playlist");
        Playlist playlist = new Playlist();
        playlist.setName("Nova Playlist");
        PlaylistResponseDTO dto = new PlaylistResponseDTO(1L, "Nova Playlist");

        when(playlistService.createPlaylist("Nova Playlist")).thenReturn(playlist);
        when(playlistMapper.toDTO(playlist)).thenReturn(dto);

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", URI.create("/api/playlists/1").toString()))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nova Playlist"));
    }

    @Test
    void createPlaylist_shouldReturn400_whenInvalidName() throws Exception {
        CreatePlaylistRequestDTO request = new CreatePlaylistRequestDTO("");

        mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").isArray());
    }


    @Test
    void updatePlaylist_shouldReturn200_whenSuccess() throws Exception {
        UpdatePlaylistRequestDTO request = new UpdatePlaylistRequestDTO("Novo Nome");
        Playlist playlist = new Playlist();
        playlist.setName("Novo Nome");
        PlaylistResponseDTO dto = new PlaylistResponseDTO(1L, "Novo Nome");

        when(playlistService.updatePlaylistName(1L, "Novo Nome")).thenReturn(playlist);
        when(playlistMapper.toDTO(playlist)).thenReturn(dto);

        mockMvc.perform(put("/api/playlists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Novo Nome"));
    }

    @Test
    void updatePlaylist_shouldReturn400_whenInvalidName() throws Exception {
        UpdatePlaylistRequestDTO request = new UpdatePlaylistRequestDTO("");

        mockMvc.perform(put("/api/playlists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updatePlaylist_shouldReturn422_whenBusinessException() throws Exception {
        UpdatePlaylistRequestDTO request = new UpdatePlaylistRequestDTO("Nome Inválido");

        doThrow(new BusinessException("Erro ao atualizar playlist"))
                .when(playlistService).updatePlaylistName(1L, "Nome Inválido");

        mockMvc.perform(put("/api/playlists/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro ao atualizar playlist"));
    }


    @Test
    void deletePlaylist_shouldReturn204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/playlists/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePlaylist_shouldReturn422_whenBusinessException() throws Exception {
        doThrow(new BusinessException("Nao foi possível deletar a playlist"))
                .when(playlistService).deletePlaylist(1L);

        mockMvc.perform(delete("/api/playlists/1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Nao foi possível deletar a playlist"));
    }

    @Test
    void addTrackToPlaylist_shouldReturn200_whenSuccess() throws Exception {
        AddTrackRequestDTO request = new AddTrackRequestDTO(10L);
        Playlist playlist = new Playlist();
        playlist.setName("Favoritas");
        PlaylistDetailsDTO dto = new PlaylistDetailsDTO(1L, "Favoritas", List.of());

        when(playlistService.addTrackInPlaylist(1L, 10L)).thenReturn(playlist);
        when(playlistMapper.toDetailsDTO(playlist)).thenReturn(dto);

        mockMvc.perform(post("/api/playlists/1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Favoritas"));
    }

    @Test
    void addTrackToPlaylist_shouldReturn400_whenInvalidRequest() throws Exception {
        AddTrackRequestDTO request = new AddTrackRequestDTO(null);

        mockMvc.perform(post("/api/playlists/1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addTrackToPlaylist_shouldReturn422_whenBusinessException() throws Exception {
        AddTrackRequestDTO request = new AddTrackRequestDTO(10L);

        doThrow(new BusinessException("Não foi possível adicionar a faixa"))
                .when(playlistService).addTrackInPlaylist(1L, 10L);

        mockMvc.perform(post("/api/playlists/1/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Não foi possível adicionar a faixa"));
    }


    @Test
    void deleteTrackFromPlaylist_shouldReturn204_whenSuccess() throws Exception {
        mockMvc.perform(delete("/api/playlists/1/tracks/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTrackFromPlaylist_shouldReturn422_whenBusinessException() throws Exception {
        doThrow(new BusinessException("Erro ao remover música"))
                .when(playlistService).removeTrackFromPlaylist(1L, 10L);

        mockMvc.perform(delete("/api/playlists/1/tracks/10"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Erro ao remover música"));
    }
}
