package com.DenserMusic.DenserMusic.Principal;

import com.DenserMusic.DenserMusic.model.Artista;
import com.DenserMusic.DenserMusic.service.ConsultaDeezerService;

import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsultaDeezerService deezerService;

    public Principal(ConsultaDeezerService deezerService) {
        this.deezerService = deezerService;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    ========== DENSER MUSIC ==========
                    
                    1- Buscar artistas
                    2- Buscar músicas
                    3- Sua Biblioteca
                    4- Perfil
                    5- Pesquisar sobre um artista
                    
                    0 - SAIR
                    """;

            System.out.println(menu);
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    buscarArtista();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao inválida!");
            }
        }

    }

    private void buscarArtista() {
        System.out.println("Informe o nome do artista que deseja buscar: ");
        var nomeDoArtista = scanner.nextLine();

        Optional<Artista> artistaOptional = deezerService.buscaArtista(nomeDoArtista);
        if (artistaOptional.isPresent()) {
            System.out.println("INFORMACOES DO ARTISTA: " + nomeDoArtista);
            System.out.println(artistaOptional.get());
        } else {
            System.out.println("Artista não encontrado!");
        }
    }
}
