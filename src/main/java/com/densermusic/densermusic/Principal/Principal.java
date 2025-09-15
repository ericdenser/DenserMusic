package com.densermusic.densermusic.Principal;

import com.densermusic.densermusic.dto.DeezerArtistDTO;
import com.densermusic.densermusic.dto.DeezerTrackSearchResultDTO;
import com.densermusic.densermusic.exception.BusinessException;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.model.Playlist;
import com.densermusic.densermusic.model.Track;
import com.densermusic.densermusic.service.ArtistService;
import com.densermusic.densermusic.service.PlaylistService;
import com.densermusic.densermusic.service.TrackService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ArtistService artistService;
    private final PlaylistService playlistService;
    private final TrackService trackService;

    public Principal(ArtistService artistService, PlaylistService playlistService, TrackService trackService) {
        this.artistService = artistService;
        this.playlistService = playlistService;
        this.trackService = trackService;
    }

    public void exibeMenu() {
        var opcao = -1;

        while (opcao != 0) {
            var menu = """
                    ========== DENSER MUSIC ==========
                    
                    1- Buscar artistas
                    2- Buscar músicas
                    3- Criar playlist
                    4- Gerenciar playlist
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
        List<Track> trackSalvas = trackService.carregarTracksSalvas();
        List<Artist> artistasSalvos = artistService.carregarArtistasSalvos();
        List<Playlist> playlistsSalvas = playlistService.carregarPlaylistsSalvas();

        System.out.println("Musicas salvas: " + trackSalvas.size() +
                "   |   " + "Playlists salvas: " + playlistsSalvas.size() +
                "   |   " + "Artistas salvos: " + artistasSalvos.size());
        artistasSalvos.forEach(System.out::println);
        playlistsSalvas.forEach(System.out::println);
        trackSalvas.forEach(System.out::println);


    }

    private void gerenciarPlaylist() {
        try {
            var playlists = playlistService.carregarPlaylistsSalvas(); //pega todas as playlists salvas no banco de dados

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

                playlistOptional = playlistService.buscarPlaylist(idPlaylistEscolhida); // Tenta buscar no banco
                if (playlistOptional.isEmpty()) { // se nao encontrar, id da playlist errado (nao existe)
                    System.out.println("ID de playlist inválido! Tente novamente.");
                }
            }

            Playlist playlistEscolhida = playlistOptional.get(); // playlist escolhida recebe o optional apos validacao

            System.out.println("\nO que você deseja fazer com a playlist '" + playlistEscolhida.getName() + "'?");
            System.out.println("1 - Adicionar uma música");
            System.out.println("2 - Remover uma música");
            System.out.println("3 - Apagar playlist");
            System.out.print("Informe a opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:

                    adicionarMusicaNaPlaylist(playlistEscolhida);
                    break;

                case 2:

                    removerMusicaDaPlaylist(playlistEscolhida);
                    break;

                case 3:

                    playlistService.deletarPlaylist(playlistEscolhida.getId());
                    break;
            }
        } catch (IllegalArgumentException | BusinessException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removerMusicaDaPlaylist(Playlist playlistEscolhida) {
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

            if (trackParaRemoverOptional.isEmpty()) {//se a variavel ainda estiver vazia, deezerId incorreto
                System.out.println("ID de música inválido! Tente novamente.");
            }
        }
        //service continua a execucao da logica
        playlistService.removerTrackDaPlaylist(playlistEscolhida.getId(), trackParaRemoverOptional.get().getId() );
    }

    private void adicionarMusicaNaPlaylist(Playlist playlistEscolhida) {
        System.out.println("\nPerfeito! Estas sao suas musicas ainda nao adicionadas na " + playlistEscolhida.getName());
        List<Track> avaiableTracks = playlistService.findAvailableTracksForPlaylist(playlistEscolhida.getId()); // pega todas tracks salvas no banco

        if (avaiableTracks.isEmpty()) { // caso nao haja nenhuma track disponivel para adicionar
            System.out.println("Todas as músicas da sua biblioteca já estão nesta playlist!");
            return;
        }
        //imprime todas disponiveis
        avaiableTracks.forEach(t -> System.out.println(t.getId() + " - " + t.getName()));

        //VALIDAÇÃO DO ID DA TRACK
        Optional<Track> trackParaAdicionarOptional = Optional.empty();
        while(trackParaAdicionarOptional.isEmpty()) {
            System.out.print("Digite o ID da música: ");
            var idTrackEscolhida = scanner.nextLong();
            scanner.nextLine();

            // procura o ID escolhido nas tracks disponiveis e retorna a track para a variavel optional
            trackParaAdicionarOptional = avaiableTracks.stream()
                    .filter(t -> t.getId() == idTrackEscolhida).findFirst();

            if (trackParaAdicionarOptional.isEmpty()) { //se a variavel ainda estiver vazia, deezerId incorreto
                System.out.println("ID de música inválido! Tente novamente.");
            }
        }
        //service continua a execucao da logica
        playlistService.adicionarTrackNaPlaylist(playlistEscolhida.getId(), trackParaAdicionarOptional.get().getId());
    }

    private void novaPlaylist() {
        System.out.println("Nome da playlist: ");
        String nomePlaylist = scanner.nextLine();
        try {
            Playlist playlistCriada = playlistService.criarPlaylist(nomePlaylist);
            System.out.println("Playlist '" + playlistCriada.getName() + "'criada com sucesso!");
        } catch (IllegalArgumentException | BusinessException e) {
            System.out.println("Erro ao criar playlist: " + e.getMessage());
        }
    }

    private void buscarMusica() {
        System.out.println("Informe um nome de música: ");
        var trechoDaTrack = scanner.nextLine();
        try {
            //lista de todas tracks com o trecho correspondente
            List<DeezerTrackSearchResultDTO> tracksEncontradas = trackService.searchTracksByName(trechoDaTrack);

            System.out.println("Foram encontradas " + tracksEncontradas.size() + "musicas. Qual delas você buscava?");

            //imprime todas tracks
            for (int i = 0; i < tracksEncontradas.size(); i++) {
                var trackAtual = tracksEncontradas.get(i);
                System.out.println((1 + i) + " - " + trackAtual.title() + " " + trackAtual.artist().name());

            }

            System.out.println("0 - Cancelar");

            // VALIDACAO DE ENTRADA
            while (true) {
                System.out.println("Digite sua opcao: ");
                int escolha = scanner.nextInt();
                scanner.nextLine();

                if (escolha == 0) {
                    break;
                }
                if (escolha >= 1 && escolha <= tracksEncontradas.size()) {
                    //salva track especifica com base no indice da lista de tracks encontradas
                    DeezerTrackSearchResultDTO trackEscolhida = tracksEncontradas.get(escolha - 1);

                    //service continua a execucao da logica
                    trackService.findOrCreateTrack(trackEscolhida);

                    break;
                }
                System.out.println("Escolha inválida, tente novamente!");
            }
        } catch (IllegalArgumentException | BusinessException e) {
            System.out.println("Erro ao buscar musica: " + e.getMessage());
        }
    }

    private void buscarArtista() {
        System.out.println("Informe o nome do artista que deseja buscar: ");
        var nomeDoArtista = scanner.nextLine();

        try {
            //lista de todos os artistas com nome correspondente
            List<DeezerArtistDTO> foundArtists = artistService.searchArtistsByName(nomeDoArtista);

            System.out.println("Foram encontrados " + foundArtists.size() + " artistas. Qual deles você deseja salvar?");
            //imprime deezerId e nome de todos artistas encontrados
            for (int i = 0; i < foundArtists.size(); i++) {
                System.out.println((1 + i) + " - " + foundArtists.get(i).name());
            }

            // menu para prosseguir
            System.out.println("""
                0 - Cancelar
                Digite sua opcao:
                """);
            int escolha = scanner.nextInt();
            scanner.nextLine();

            if (escolha > 0 && escolha <= foundArtists.size()) {
                //salva o artista especifico com base no indice da lista de todos artistas encontrados
                Artist novoArtista = artistService.findOrCreateArtist(foundArtists.get(escolha - 1).deezerId());
            } else if (escolha == 0){
                System.out.println("Operação cancelada.");
            } else {
                System.out.println("Opção inválida.");
            }
        } catch (IllegalArgumentException | BusinessException e) {
            System.out.println("Erro " + e.getMessage());
        }

    }

}

