package com.densermusic.densermusic;

import com.densermusic.densermusic.Principal.Principal;
import com.densermusic.densermusic.repository.ArtistRepository;
import com.densermusic.densermusic.repository.TrackRepository;
import com.densermusic.densermusic.repository.PlaylistRepository;
import com.densermusic.densermusic.service.ArtistService;
import com.densermusic.densermusic.service.PlaylistService;
import com.densermusic.densermusic.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DenserMusicApplication implements CommandLineRunner {

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private TrackRepository trackRepository;

	@Autowired
	private PlaylistRepository playlistRepository;

	@Autowired
	private ArtistService artistService;

	@Autowired
	private PlaylistService playlistService;

	@Autowired
	private TrackService trackService;

	public static void main(String[] args) {
		SpringApplication.run(DenserMusicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(artistRepository, trackRepository,
				playlistRepository, artistService, playlistService, trackService);
		principal.exibeMenu();
	}
}
