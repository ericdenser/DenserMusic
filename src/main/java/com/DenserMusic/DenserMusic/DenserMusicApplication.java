package com.DenserMusic.DenserMusic;

import com.DenserMusic.DenserMusic.Principal.Principal;
import com.DenserMusic.DenserMusic.repository.ArtistRepository;
import com.DenserMusic.DenserMusic.repository.TrackRepository;
import com.DenserMusic.DenserMusic.repository.PlaylistRepository;
import com.DenserMusic.DenserMusic.service.ConsultaDeezerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DenserMusicApplication implements CommandLineRunner {

	@Autowired
	private ConsultaDeezerService deezerService;

	@Autowired
	private ArtistRepository artistRepository;

	@Autowired
	private TrackRepository trackRepository;

	@Autowired
	private PlaylistRepository playlistRepository;

	public static void main(String[] args) {
		SpringApplication.run(DenserMusicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(deezerService, artistRepository, trackRepository, playlistRepository);
		principal.exibeMenu();
	}
}
