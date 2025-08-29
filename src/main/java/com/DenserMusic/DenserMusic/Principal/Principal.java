package com.DenserMusic.DenserMusic.Principal;

import com.DenserMusic.DenserMusic.dto.DeezerTrackSearchResult;
import com.DenserMusic.DenserMusic.model.Artist;
import com.DenserMusic.DenserMusic.model.Playlist;
import com.DenserMusic.DenserMusic.model.Track;
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
                    5- Sua biblioteca
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
                    novaPlaylist();
                    break;
                case 4:
                    gerenciarPlaylist();
                    break;
                case 5:
                    biblioteca();
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

    private void biblioteca() {
    }

    private void gerenciarPlaylist() {
        var playlists = playlistRepository.findAll(); //pega todas as playlists salvas no banco de dados

        if (playlists.isEmpty()) {//se vazio, nao conseguiu encontrar nenhuma salva
            System.out.println("Nenhuma playlist criada ainda. Crie uma primeiro.");
            return;
        }

        System.out.println("Suas playlists criadas: ");
        //imprime todas playlists criadas
        playlists.forEach(p -> System.out.println(p.getId() + " - " + p.getName()));

        //VALIDAÇÃO DO ID DA PLAYLIST
        Optional<Playlist> playlistOptional = Optional.empty(); // inicializa optinal vazio
        while (playlistOptional.isEmpty()) {

            System.out.print("Digite o ID da playlist que deseja gerenciar: ");
            var idPlaylistEscolhida = scanner.nextLong();
            scanner.nextLine();

            playlistOptional = playlistRepository.findById(idPlaylistEscolhida); // Tenta buscar no banco
            if (playlistOptional.isEmpty()) { // se nao encontrar, id nao existe
                System.out.println("ID de playlist inválido! Tente novamente.");
            }
        }

        Playlist playlistEscolhida = playlistOptional.get(); // playlist escolhida recebe o optional apos validacao

        System.out.println("\nO que você deseja fazer com a playlist '" + playlistEscolhida.getName() + "'?");
        System.out.println("1 - Adicionar uma música");
        System.out.println("2 - Remover uma música");
        System.out.print("Informe a opção: ");

        int opcao = scanner.nextInt();
        scanner.nextLine();

        switch (opcao) {
            case 1:

                System.out.println("\nPerfeito! Estas sao suas musicas ainda nao adicionadas na " + playlistEscolhida.getName());
                List<Track> tracksNaBiblioteca = trackRepository.findAll(); // pega todas tracks salvas no banco

                //filtra para mostrar apenas as que ainda nao foram salvas na playlist
                List<Track> tracksDisponiveis = tracksNaBiblioteca.stream()
                                .filter(track -> !playlistEscolhida.getTracksOfPlaylist().contains(track))
                                .toList();

                if (tracksDisponiveis.isEmpty()) { // caso nao haja nenhuma track disponivel para adicionar
                    System.out.println("Todas as músicas da sua biblioteca já estão nesta playlist!");
                    return;
                }
                //imprime todas disponiveis
                tracksDisponiveis.forEach(t -> System.out.println(t.getId() + " - " + t.getName()));

                //VALIDAÇÃO DO ID DA TRACK
                Optional<Track> trackParaAdicionarOptional = Optional.empty();
                while(trackParaAdicionarOptional.isEmpty()) {
                    System.out.print("Digite o ID da música: ");
                    var idTrackEscolhida = scanner.nextLong();
                    scanner.nextLine();

                    // procura o ID escolhido nas tracks disponiveis e retorna a track para a variavel optional
                    trackParaAdicionarOptional = tracksDisponiveis.stream()
                            .filter(t -> t.getId() == idTrackEscolhida).findFirst();

                    if (trackParaAdicionarOptional.isEmpty()) { //se a variavel ainda estiver vazia, id incorreto
                        System.out.println("ID de música inválido! Tente novamente.");
                    }
                }
                //service continua a execucao da logica
                deezerService.adicionarTrackNaPlaylist(playlistEscolhida.getId(), trackParaAdicionarOptional.get().getId());

                break;

            case 2:
                //lista com as tracks ja salvas na playlist
                List<Track> tracksDaPlaylist = playlistEscolhida.getTracksOfPlaylist();

                if (tracksDaPlaylist.isEmpty()) {// se estiver vazia, nao tem nada para remover
                    System.out.println("Esta playlist não contém músicas para remover.");
                    return;
                }

                System.out.println("\nPerfeito! Agora escolha a música para remover: ");
                //imprime todas tracks da playlist
                tracksDaPlaylist.forEach(t -> System.out.println(t.getId() + " - " + t.getName()));

                //VALIDAÇÃO DO ID DA TRACK
                Optional<Track> trackParaRemoverOptional = Optional.empty();
                while(trackParaRemoverOptional.isEmpty()) {
                    System.out.print("Digite o ID da música: ");
                    var idTrackRemover = scanner.nextLong();
                    scanner.nextLine();
                    // mesma logica que o case 1, procura o ID escolhido nas tracks disponiveis e retorna a track para a variavel optional
                    trackParaRemoverOptional = tracksDaPlaylist.stream()
                            .filter(track -> track.getId() == idTrackRemover).findFirst();

                    if (trackParaRemoverOptional.isEmpty()) {//se a variavel ainda estiver vazia, id incorreto
                        System.out.println("ID de música inválido! Tente novamente.");
                    }
                }
                //service continua a execucao da logica
                deezerService.removerTrackDaPlaylist(playlistEscolhida.getId(), trackParaRemoverOptional.get().getId() );

                break;
        }


    }

    private void novaPlaylist() {
        System.out.println("Nome da playlist: ");
        String nomePlaylist = scanner.nextLine();
        try {
            //service cuida da logica de instanciar playlist e salvar no banco
            deezerService.criarPlaylist(nomePlaylist);
            System.out.println("Playlist " + nomePlaylist + " criada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void buscarMusica() {
        System.out.println("Informe um nome de música: ");
        var trechoDaTrack = scanner.nextLine();
        try {
            //lista de todas tracks com o trecho correspondente
            List<DeezerTrackSearchResult> tracksEncontradas = deezerService.buscaTracksPorNome(trechoDaTrack);
            if (tracksEncontradas.isEmpty()) {
                System.out.println("Nenhuma música encontrada.");
                return;
            }
            System.out.println("Foram encontradas " + tracksEncontradas.size() + "musicas. Qual delas você buscava?");
            //imprime todas tracks
            for (int i = 0; i < tracksEncontradas.size(); i++) {
                var trackAtual = tracksEncontradas.get(i);
                System.out.println((1 + i) + " - " + trackAtual.title() + " " + trackAtual.artist().name());

            }

            System.out.println("0 - Cancelar");

            int escolha = -1;

            // VALIDACAO DE ENTRADA
            while (escolha <= 0 || escolha >= tracksEncontradas.size()) {
                System.out.println("Digite sua opcao: ");
                escolha = scanner.nextInt();
                scanner.nextLine();

                if (escolha > 0 && escolha <= tracksEncontradas.size()) {
                    //salva track especifica com base no indice da lista de tracks encontradas
                    DeezerTrackSearchResult trackEscolhida = tracksEncontradas.get(escolha - 1);

                    //service continua a execucao da logica
                    deezerService.buscaESalvaTrack(trackEscolhida);

                } else {
                    System.out.println("Escolha inválida, tente novamente!");
                }
            }
        } catch (Exception e) {
            System.out.println("Detalhe técnico: " + e.getMessage());
        }
    }

    private void buscarArtista() {
        System.out.println("Informe o nome do artista que deseja buscar: ");
        var nomeDoArtista = scanner.nextLine();

        //lista de todos artistas com nome correspondente
        List<Artist> artistasEncontrados = deezerService.buscaArtistasPorNome(nomeDoArtista);


        if (artistasEncontrados.isEmpty()) {//lista vazia, nenhum retorno
            System.out.println("Nenhum artista encontrado com o nome '" + nomeDoArtista + "'.");
            return;
        }

        System.out.println("Foram encontrados " + artistasEncontrados.size() + " artistas. Qual deles você deseja salvar?");
        //imprime id e nome de todos artistas encontrados
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
            //salva o artista especifico com base no indice da lista de todos artistas encontrados
            Artist artistaEscolhido = artistasEncontrados.get(escolha - 1);

            //optional para tentar buscar no banco
            Optional<Artist> artistaNoBanco = artistRepository.findByNameIgnoreCase(artistaEscolhido.getName());
            if(artistaNoBanco.isPresent()) { // se ja estiver salvo
                System.out.println("\n" + artistaEscolhido.getName() + " já está salvo na sua biblioteca!");
                System.out.println("INFORMAÇÕES DO ARTISTA:");
                System.out.println(artistaNoBanco.get());
            } else {
                // caso ainda nao tenha sido salvo
                try {
                    artistRepository.save(artistaEscolhido);//salva no banco
                    System.out.println("Artista salvo no banco de dados com sucesso!");
                } catch (DataIntegrityViolationException e) { // tratamento de erro
                    System.out.println("ERRO: " + e.getMessage());
                }
            }
        } else if (escolha == 0){
            System.out.println("Operação cancelada.");
        } else {
            System.out.println("Opção inválida.");
        }
    }

}

