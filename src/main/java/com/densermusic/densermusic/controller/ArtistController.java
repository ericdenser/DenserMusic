package com.densermusic.densermusic.controller;


import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.service.ArtistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.densermusic.densermusic.dto.artistDTO.ArtistResponseDTO;
import com.densermusic.densermusic.mapper.ArtistMapper;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*")
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistMapper artistMapper;


    public ArtistController(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponseDTO>> loadSavedArtists() {
        List<Artist> artists = artistService.loadSavedArtists();

        List<ArtistResponseDTO> dtos = artistMapper.toDTOList(artists);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeezerArtistDTO>> searchArtistsByName(@RequestParam ("name") String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O parâmetro 'name' é obrigatório");
        }

        List<DeezerArtistDTO> results = artistService.searchArtistsByName(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistResponseDTO> getArtistById(@PathVariable Long id) {
        return ResponseEntity.of(artistService.findByDbId(id).map(artistMapper::toDTO));
    }


    @PostMapping
    public ResponseEntity<ArtistResponseDTO> createArtist(@RequestBody @Valid CreateArtistRequestDTO request) {
        CreationResultDTO<Artist> result = artistService.findOrCreateArtist(request);

        ArtistResponseDTO responseDTO = artistMapper.toDTO(result.entity());

        if(result.created()) {
            URI location = URI.create("/api/artists/" + responseDTO.id());
            return ResponseEntity.created(location).body(responseDTO);
        } else {
            //ja existia, retornamos 200 OK.
            return ResponseEntity.ok(responseDTO);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistByDbId(id);
        return ResponseEntity.noContent().build();
    }

}
