package com.densermusic.densermusic.service;

import com.densermusic.densermusic.client.DeezerClient;
import com.densermusic.densermusic.dto.CreationResultDTO;
import com.densermusic.densermusic.dto.artistDTO.CreateArtistRequestDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistDTO;
import com.densermusic.densermusic.dto.artistDTO.DeezerArtistSearchResponseDTO;
import com.densermusic.densermusic.model.Artist;
import com.densermusic.densermusic.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import com.densermusic.densermusic.exception.BusinessException;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

        // 1. Arrange
        String artistName = "Queen";

        // crio os objetos DTO que espero que a API retorne
        DeezerArtistDTO artistDto1 = new DeezerArtistDTO("Queen", "picture_url", 75L, 1000000);
        DeezerArtistDTO artistDto2 = new DeezerArtistDTO("The Queen", "beatles_pic", 1L, 2000000);

        DeezerArtistSearchResponseDTO mockResponse = new DeezerArtistSearchResponseDTO(List.of(artistDto1, artistDto2));

        when(deezerClient.searchArtistByName(artistName))
                .thenReturn(mockResponse);

        // Act
        List<DeezerArtistDTO> result = artistService.searchArtistsByName(artistName);

        // Assert
        assertNotNull(result); // o resultado nao deve ser nulo
        assertFalse(result.isEmpty()); // a lista nao deve estar vazia
        assertEquals(2, result.size()); // o tamanho da lista deve ser 2
        assertEquals("Queen", result.getFirst().name()); // o nome do artista no primeiro result deve ser "Queen"
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

    @Test
    void findOrCreateArtist_shouldReturnExistingArtist_whenArtistIsAlreadyInDb() {

        // Arrange
        Long apiId = 27L;
        CreateArtistRequestDTO request = new CreateArtistRequestDTO(apiId);

        Artist existingArtist = new Artist("Pearl Jam", "pc_url", 10000, 27L);

        when(artistRepository.findByApiId(apiId))
                .thenReturn(Optional.of(existingArtist));

        // Act
        CreationResultDTO<Artist> result = artistService.findOrCreateArtist(request);

        // Assert
        assertNotNull(result); // nao pode ser nulo
        assertFalse(result.created()); // created deve ser false
        assertEquals("Pearl Jam", result.entity().getName());

        // cliente da API nao deve ter sido chamado
        verify(deezerClient, never()).searchArtistById(anyLong());
        // metodo save do repositorio nao deve ter sido chamado
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    void findOrCreateArtist_shouldCreateAndReturnNewArtist_whenArtistIsNotInDb() {
        // Arrange
        Long apiId = 27L;
        CreateArtistRequestDTO request = new CreateArtistRequestDTO(apiId);

        //Dto que API vai retornar
        DeezerArtistDTO apiDto = new DeezerArtistDTO("Pearl Jam", "pic_url", apiId, 10000);


        //Artista que o repositorio vai retornar depois de salvar
        Artist savedArtist = new Artist("Pearl Jam","pic_url", 10000, apiId);

        when(artistRepository.findByApiId(apiId))
                .thenReturn(Optional.empty());

        when(deezerClient.searchArtistById(apiId))
                .thenReturn(apiDto);

        when(artistRepository.save(any(Artist.class)))
                .thenReturn(savedArtist);

        // Act
        CreationResultDTO<Artist> result = artistService.findOrCreateArtist(request);

        assertNotNull(result); // nao pode ser nulo
        assertTrue(result.created()); // created deve ser true
        assertEquals("Pearl Jam", result.entity().getName());

        // salva apenas 1 vez no banco
        verify(artistRepository, times(1)).save(any(Artist.class));

        // procura na api apenas 1 vez
        verify(deezerClient, times(1)).searchArtistById(apiId);
    }

    @Test
    void findByApiId_shouldReturnArtist_whenArtistExists() {
        //arrange
        Long apiId = 10L;
        Artist artist = new Artist("TestArtist", "url", 123, apiId);


        when(artistRepository.findByApiId(apiId)).thenReturn(Optional.of(artist));

        //act
        Optional<Artist> result = artistService.findByApiId(apiId);


        //assert
        assertTrue(result.isPresent());
        assertEquals("TestArtist", result.get().getName());
    }

    @Test
    void findByApiId_shouldReturnEmpty_whenArtistDoesNotExist() {

        //arrange
        when(artistRepository.findByApiId(anyLong()))
                .thenReturn(Optional.empty());


        //act
        Optional<Artist> result = artistService.findByApiId(999L);

        //assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findByDbId_shouldReturnArtist_whenArtistExists() {

        //arrange
        Long id = 1L;
        Artist artist = new Artist("TestArtist", "url", 123, 55L);

        when(artistRepository.findById(id))
                .thenReturn(Optional.of(artist));


        //act
        Optional<Artist> result = artistService.findByDbId(id);

        //assert
        assertTrue(result.isPresent());
        assertEquals("TestArtist", result.get().getName());
    }

    @Test
    void findByDbId_shouldReturnEmpty_whenArtistDoesNotExist() {

        //assert
        when(artistRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //act
        Optional<Artist> result = artistService.findByDbId(999L);

        //assert
        assertTrue(result.isEmpty());
    }

    @Test
    void loadSavedArtists_shouldReturnArtistList() {
        //arrange
        List<Artist> artistList = List.of(new Artist(), new Artist());
        when(artistRepository.findAll())
                .thenReturn(artistList);

        //act
        List<Artist> result = artistService.loadSavedArtists();


        //assert
        assertEquals(2, result.size());
    }

    @Test
    void save_shouldThrowException_whenArtistAlreadyExists() {

        //arrange
        Artist artist = new Artist("DupArtist", "url", 100, 10L);

        when(artistRepository.findByApiId(artist.getApiId()))
                .thenReturn(Optional.of(artist));

        // act + assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            artistService.save(artist);
        });

        assertEquals("Artista com API ID 10 já existe.", exception.getMessage());
    }

    @Test
    void save_shouldSaveArtist_whenArtistDoesNotExist() {

        //arrange
        Artist artist = new Artist("NewArtist", "url", 100, 10L);

        when(artistRepository.findByApiId(artist.getApiId()))
                .thenReturn(Optional.empty());
        when(artistRepository.save(artist))
                .thenReturn(artist);

        //act
        Artist saved = artistService.save(artist);

        //assert
        assertEquals("NewArtist", saved.getName());
        verify(artistRepository, times(1)).save(artist);
    }

    @Test
    void deleteArtistByDbId_shouldThrowException_whenArtistDoesNotExist() {
        //arrange
        Long id = 5L;
        when(artistRepository.existsById(id))
                .thenReturn(false);

        // act + assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            artistService.deleteArtistByDbId(id);
        });

        assertEquals("Artista com ID 5 não encontrado, não foi possível deletar.", exception.getMessage());
    }

    @Test
    void deleteArtistByDbId_shouldDeleteArtist_whenArtistExists() {
        //arrange
        Long id = 5L;
        when(artistRepository.existsById(id))
                .thenReturn(true);

        //act
        artistService.deleteArtistByDbId(id);

        //assert
        verify(artistRepository, times(1)).deleteById(id);
    }
}
