package com.densermusic.densermusic.controller;


import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.service.ArtistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;


    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<Artist> loadSavedArtists() {
        return artistService.carregarArtistasSalvos();
    }

    @GetMapping("/search")
    public List<DeezerArtistDTO> searchArtistsByName(@RequestParam("name") String name) {
        return artistService.searchArtistsByName(name);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Long id) {
        Optional<Artist> artistOptional = artistService.findByDbId(id);
        if(artistOptional.isPresent()) {
            return ResponseEntity.ok(artistOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Artist> createArtist(@RequestBody CreateArtistRequestDTO request) {
        CreationResultDTO<Artist> result = artistService.findOrCreateArtist(request);
        if(result.created()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.entity());
        } else {
            //ja existia, retornamos 200 OK.
            return ResponseEntity.ok(result.entity());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtistByDbId(id);
        return ResponseEntity.noContent().build();
    }

}
