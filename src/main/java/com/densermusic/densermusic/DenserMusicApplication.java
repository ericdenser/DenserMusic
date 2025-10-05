package com.densermusic.densermusic;

import com.densermusic.densermusic.Principal.Principal;
import org.springframework.beans.factory.annotation.Autowired;
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
