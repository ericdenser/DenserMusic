package com.densermusic.densermusic;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DenserMusicApplication implements CommandLineRunner {

	//private final Principal principal;


	public static void main(String[] args) {
		SpringApplication.run(DenserMusicApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//this.principal.exibeMenu();
	}
}
