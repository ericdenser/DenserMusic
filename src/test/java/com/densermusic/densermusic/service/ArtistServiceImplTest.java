package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// anotação que "liga" o Mockito
@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock //"dublê" de DeezerClient (nao fará chamadas reais na internet)
    private DeezerClient deezerClient;

    @Mock // "dublê" de ArtistRepository (ele nao tocará no banco de dados)
    private ArtistRepository artistRepository;

    @InjectMocks // cria uma instância REAL do ArtistServiceImpl, mas injeta os dublês acima nela.
    private ArtistServiceImpl artistService;

    @Test
    void searchArtistsByName_shouldReturnArtistList_whenSearchIsSuccessful() {
        // padrao de teste (AAA): Arrange, Act, Assert

        // 1. Arrange (preparar o cenário)
        String artistName = "Queen";

        // crio os objetos DTO que espero que a API retorne
        DeezerArtistDTO artistDto1 = new DeezerArtistDTO("Queen", "picture_url", 75L, 1000000);
        DeezerArtistDTO artistDto2 = new DeezerArtistDTO("The Queen", "beatles_pic", 1L, 2000000);

        DeezerArtistSearchResponseDTO mockResponse = new DeezerArtistSearchResponseDTO(List.of(artistDto1, artistDto2));

        // dizendo ao dublê: "QUANDO o metodo searchArtistByName do deezerClient for chamado com "Queen"
        when(deezerClient.searchArtistByName(artistName))
                .thenReturn(mockResponse); // "ENTAO retorne este objeto de resposta"

        // 2. Act (executar a acao)
        // chamo o metodo real da service que chamará o deezerClient
        List<DeezerArtistDTO> result = artistService.searchArtistsByName(artistName);

        // 3. Assert (verificar o resultado)
        assertNotNull(result); // o resultado nao deve ser nulo
        assertFalse(result.isEmpty()); // a lista nao deve estar vazia
        assertEquals(2, result.size()); // o tamanho da lista deve ser 2
        assertEquals("Queen", result.get(0).name()); // o nome do artista no resultado deve ser "Queen"
    }

    @Test
    void searchArtistsByName_shouldReturnEmptyList_whenNoResultsFound() {
        // Arrange
        String artistName = "NomeInexistente123";
        DeezerArtistSearchResponseDTO mockResponse = new DeezerArtistSearchResponseDTO(Collections.emptyList());
        when(deezerClient.searchArtistByName(artistName)).thenReturn(mockResponse);

        // Act
        List<DeezerArtistDTO> result = artistService.searchArtistsByName(artistName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}