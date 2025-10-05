package com.densermusic.densermusic.service;

import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.trackDTO.CreateTrackRequestDTO;
import com.densermusic.densermusic.dto.trackDTO.DeezerTrackSearchResultDTO;
import com.densermusic.densermusic.model.Track;

import java.util.List;
import java.util.Optional;

public interface TrackService {
    Track save(Track track);

    CreationResultDTO<Track> findOrCreateTrack(CreateTrackRequestDTO request);

    List<DeezerTrackSearchResultDTO> searchTracksByName(String trackName);

    Optional<Track> findTrackByDbId(Long id);

    Optional<Track> findTrackByApiId(Long apiId);

    List<Track> carregarTracksSalvas();

    void deleteTrackByDbId(Long id);

}
