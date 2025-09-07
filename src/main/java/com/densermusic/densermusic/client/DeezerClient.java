package com.densermusic.densermusic.client;

import com.densermusic.densermusic.dto.DeezerArtist;
import com.densermusic.densermusic.dto.DeezerArtistSearchResponse;
import com.densermusic.densermusic.dto.DeezerTrack;
import com.densermusic.densermusic.dto.DeezerTrackSearchResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface DeezerClient {
    @GetExchange("/search/artist")
    DeezerArtistSearchResponse searchArtistByName(@RequestParam("q") String artistName);

    @GetExchange("/artist/{id}")
    DeezerArtist searchArtistById(@PathVariable("id") Long artistId);

    @GetExchange("/track/{id}") // <- placeholder "deezerId"
    DeezerTrack getTrackById(@PathVariable("id") Long trackId); // receba um trackId e substitua-o no placeholder {deezerId}

    @GetExchange("/search/track")
    DeezerTrackSearchResponse getTrackByName(@RequestParam("q") String trackName);
}