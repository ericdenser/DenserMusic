package com.DenserMusic.DenserMusic.Principal;

import com.DenserMusic.DenserMusic.model.Artista;
import com.DenserMusic.DenserMusic.repository.ArtistaRepository;
import com.DenserMusic.DenserMusic.service.ConsultaDeezerService;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final ConsultaDeezerService deezerService;
    private final ArtistaRepository repository;

    public Principal(ConsultaDeezerService deezerService, ArtistaRepository repository) {
        this.deezerService = deezerService;
        this.repository = repository;
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

        List<Artista> artistasEncontrados = deezerService.buscaArtistasPorNome(nomeDoArtista);


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
                Artista artistaEscolhido = artistasEncontrados.get(escolha - 1);

                Optional<Artista> artistaNoBanco = repository.findByNameIgnoreCase(artistaEscolhido.getName());
                if(artistaNoBanco.isPresent()) {
                    System.out.println("\n" + artistaEscolhido.getName() + " já está salvo na sua biblioteca!");
                    System.out.println("INFORMAÇÕES DO ARTISTA:");
                    System.out.println(artistaNoBanco.get());
                } else {
                    try {
                        repository.save(artistaEscolhido);
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
