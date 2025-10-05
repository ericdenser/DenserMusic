package com.densermusic.densermusic.client;

import com.densermusic.densermusic.dto.DeezerArtistDTO;
import com.densermusic.densermusic.dto.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.dto.DeezerTrackDTO;
import com.densermusic.densermusic.dto.DeezerTrackSearchResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface DeezerClient {
    @GetExchange("/search/artist")
    DeezerArtistSearchResponseDTO searchArtistByName(@RequestParam("q") String artistName);

    @GetExchange("/artist/{id}")
    DeezerArtistDTO searchArtistById(@PathVariable("id") Long artistId);

    @GetExchange("/track/{id}") // <- placeholder "deezerId"
    DeezerTrackDTO getTrackById(@PathVariable("id") Long trackId); // receba um trackId e substitua-o no placeholder {id}

    @GetExchange("/search/track")
    DeezerTrackSearchResponseDTO getTrackByName(@RequestParam("q") String trackName);
}