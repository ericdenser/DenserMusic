package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.trackDTO.CreateTrackRequestDTO;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackSearchResultDTO;
import com.densermusic.densermusic.dto.trackDTO.TrackResponseDTO;
import com.densermusic.densermusic.mapper.TrackMapper;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
public class TrackController {

    private final TrackService trackService;
    private final TrackMapper trackMapper;

    public TrackController(TrackService trackService, TrackMapper trackMapper) {
        this.trackService = trackService;
        this.trackMapper = trackMapper;
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeezerTrackSearchResultDTO>> searchTracksByName(@RequestParam("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O parâmetro 'name' é obrigatório");
        }

        List<DeezerTrackSearchResultDTO> results = trackService.searchTracksByName(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping
    public ResponseEntity<List<TrackResponseDTO>> loadAllSavedTracks() {
        List<Track> tracks = trackService.carregarTracksSalvas();

        List<TrackResponseDTO> dtos = trackMapper.toDTOList(tracks);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponseDTO> getTrackById(@PathVariable Long id) {
        return ResponseEntity.of(trackService.findTrackByDbId(id).map(trackMapper::toDTO));
    }


    @PostMapping
    public ResponseEntity<TrackResponseDTO> createTrack(@RequestBody @Valid CreateTrackRequestDTO request) {
        CreationResultDTO<Track> result = trackService.findOrCreateTrack(request);

        TrackResponseDTO responseDTO = trackMapper.toDTO(result.entity());

        if(result.created()) {
            URI location = URI.create("/api/tracks/" + responseDTO.id());
            return ResponseEntity.created(location).body(responseDTO);
        } else {
            //ja existia, retornamos 200 OK.
            return ResponseEntity.ok(responseDTO);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrackByDbId(id);
        return ResponseEntity.noContent().build();
    }
}
