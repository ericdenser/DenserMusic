package com.DenserMusic.DenserMusic.client;

import com.DenserMusic.DenserMusic.dto.DeezerArtistSearchResponse;
import com.DenserMusic.DenserMusic.dto.DeezerTrack;
import com.DenserMusic.DenserMusic.dto.DeezerTrackSearchResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface DeezerClient {
    @GetExchange("/search/artist")
    DeezerArtistSearchResponse searchArtistByName(@RequestParam("q") String artistName);

    @GetExchange("/track/{id}") // <- placeholder "id"
    DeezerTrack getTrackById(@PathVariable("id") Long trackId); // receba um trackId e substitua-o no placeholder {id}

    @GetExchange("/search/track")
    DeezerTrackSearchResponse searchTrackByName(@RequestParam("q") String trackName);
}