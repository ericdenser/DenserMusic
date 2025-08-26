package com.DenserMusic.DenserMusic.client;

import com.DenserMusic.DenserMusic.dto.DeezerResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface DeezerClient {
    @GetExchange("/search/artist")
    DeezerResponse searchArtistByName(@RequestParam("q") String artistName);
}