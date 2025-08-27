package com.DenserMusic.DenserMusic.Principal;

import com.DenserMusic.DenserMusic.dto.DeezerTrackSearchResult;
import com.DenserMusic.DenserMusic.model.Artist;
import com.DenserMusic.DenserMusic.repository.ArtistRepository;
import com.DenserMusic.DenserMusic.repository.TrackRepository;
import com.DenserMusic.DenserMusic.repository.PlaylistRepository;
import com.DenserMusic.DenserMusic.service.ConsultaDeezerService;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsultaDeezerService deezerService;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    public Principal(ConsultaDeezerService deezerService, ArtistRepository artistRepository, TrackRepository trackRepository, PlaylistRepository playlistRepository) {
        this.deezerService = deezerService;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
        this.playlistRepository = playlistRepository;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    ========== DENSER MUSIC ==========
                    
                    1- Buscar artistas
                    2- Buscar músicas
                    3- Criar playlist
                    4- Adicionar músicas a uma playlist
                    5- Listas musicas salvas
                    6- Pesquisar sobre um artista
                    
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
                    buscarMusica();
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 6:
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcao inválida!");
            }
        }

    }

    private void buscarMusica() {
        System.out.println("Informe um nome de música: ");
        var trecho = scanner.nextLine();
        List<DeezerTrackSearchResult> tracksEncontradas = null;
        try {
            tracksEncontradas = deezerService.buscaTracksPorNome(trecho);
            if (tracksEncontradas.isEmpty()) {
                System.out.println("Nenhuma música encontrada.");
                return;
            }
            System.out.println("Foram encontradas " + tracksEncontradas.size() + "musicas. Qual delas você buscava?");
            for (int i = 0; i < tracksEncontradas.size(); i++) {
                var trackAtual = tracksEncontradas.get(i);
                System.out.println((1 + i) + " - " + trackAtual.title() + " " + trackAtual.artist().name());

            }

            System.out.println("""
                    0 - Cancelar
                    Digite sua opcao:
                    """);
            int escolha = scanner.nextInt();
            scanner.nextLine();

            if (escolha > 0 && escolha <= tracksEncontradas.size()) {
                DeezerTrackSearchResult trackEscolhida = tracksEncontradas.get(escolha - 1);
                long trackId = trackEscolhida.id();
                String trackArtistName = trackEscolhida.artist().name();

                Optional<Artist> artist = artistRepository.findByNameIgnoreCase(trackArtistName);

                if (artist.isPresent()) {
                    deezerService.salvarMusicaPorId(trackId, artist.get());
                    System.out.println("Música salva com sucesso na sua biblioteca!");
                } else {
                    System.out.println("O artista '" + trackArtistName + "' ainda não está na sua biblioteca. Busque e salve o artista primeiro.");
                }
            }
        } catch (Exception e) {
            System.out.println("Detalhe técnico: " + e.getMessage());
        }
    }

    private void buscarArtista() {
        System.out.println("Informe o nome do artista que deseja buscar: ");
        var nomeDoArtista = scanner.nextLine();

        List<Artist> artistasEncontrados = deezerService.buscaArtistasPorNome(nomeDoArtista);


        if (artistasEncontrados.isEmpty()) {
            System.out.println("Nenhum artista encontrado com o nome '" + nomeDoArtista + "'.");
            return;
        }
        System.out.println("Foram encontrados " + artistasEncontrados.size() + " artistas. Qual deles você deseja salvar?");
        for (int i = 0; i < artistasEncontrados.size(); i++) {
            System.out.println((1 + i) + " - " + artistasEncontrados.get(i).getName());
        }
        System.out.println("""
            0 - Cancelar
            Digite sua opcao:
            """);
        int escolha = scanner.nextInt();
        scanner.nextLine();
        if (escolha > 0 && escolha <= artistasEncontrados.size()) {
            Artist artistaEscolhido = artistasEncontrados.get(escolha - 1);

            Optional<Artist> artistaNoBanco = artistRepository.findByNameIgnoreCase(artistaEscolhido.getName());
            if(artistaNoBanco.isPresent()) {
                System.out.println("\n" + artistaEscolhido.getName() + " já está salvo na sua biblioteca!");
                System.out.println("INFORMAÇÕES DO ARTISTA:");
                System.out.println(artistaNoBanco.get());
            } else {
                try {
                    artistRepository.save(artistaEscolhido);
                    System.out.println("Artista salvo no banco de dados com sucesso!");
                } catch (DataIntegrityViolationException e) {
                    System.out.println("ERRO: Este artista já existe no banco de dados.");
                }
            }
        } else if (escolha == 0){
            System.out.println("Operação cancelada.");
        } else {
            System.out.println("Opção inválida.");
        }
    }

}

