package com.densermusic.densermusic.controller;

import com.densermusic.densermusic.dto.CreateTrackRequestDTO;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.DeezerTrackSearchResultDTO;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.service.TrackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping("/search")
    public List<DeezerTrackSearchResultDTO> searchTracksByName(@RequestParam("name") String name) {
        return trackService.searchTracksByName(name);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Track> getTrackById(@PathVariable Long id) {
        Optional<Track> trackOptional = trackService.findTrackByDbId(id);
        if (trackOptional.isPresent()) {
            return ResponseEntity.ok(trackOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Track> createTrack(@RequestBody CreateTrackRequestDTO request) {
        CreationResultDTO<Track> result = trackService.findOrCreateTrack(request);

        if(result.created()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result.entity());
        } else {
            //ja existia, retornamos 200 OK.
            return ResponseEntity.ok(result.entity());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrackByDbId(id);
        return ResponseEntity.noContent().build();
    }
}
