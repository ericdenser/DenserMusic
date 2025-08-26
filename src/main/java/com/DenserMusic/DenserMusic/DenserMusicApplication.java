package com.DenserMusic.DenserMusic;

import com.DenserMusic.DenserMusic.Principal.Principal;
import com.DenserMusic.DenserMusic.service.ConsultaDeezerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DenserMusicApplication implements CommandLineRunner {

	@Autowired
	private ConsultaDeezerService deezerService;

	public static void main(String[] args) {
		SpringApplication.run(DenserMusicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(deezerService);
		principal.exibeMenu();
	}
}
