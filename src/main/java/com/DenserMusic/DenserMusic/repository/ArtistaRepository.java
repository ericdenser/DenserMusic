package com.DenserMusic.DenserMusic.repository;

import com.DenserMusic.DenserMusic.model.Artista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {

    Optional<Artista> findByNameIgnoreCase(String name);
}
//feat: implementa busca interativa de artistas e integra persistência
//
//Este commit aprimora significativamente a funcionalidade de busca de artistas, tornando-a mais flexível e interativa, além de finalizar a integração com o banco de dados para persistir as escolhas do usuário.
//- A versão anterior exigia que o usuário soubesse o nome exato do artista. Agora, a aplicação apresenta uma lista de possíveis correspondências, dando ao usuário o poder de escolher o artista correto antes de salvá-lo em sua biblioteca pessoal.
//O que foi feito:
//- Busca por Lista: O ConsultaDeezerService foi modificado para retornar uma List<Artista>, permitindo que a aplicação receba todos os resultados relevantes da API.
//- Seleção Interativa: A classe Principal agora exibe os artistas encontrados em uma lista numerada, solicitando que o usuário escolha qual deles deseja registrar, melhorando drasticamente a experiência de uso.
//- Persistência no Banco de Dados: O artista escolhido pelo usuário agora é efetivamente salvo no banco de dados PostgreSQL, completando o fluxo de integração que havia sido preparado no commit inicial.
//- Prevenção de Duplicidade: Antes de salvar o artista, o sistema agora verifica se um registro com o mesmo nome (de forma case-insensitive) já existe no banco. Isso evita redundância e mantém a base de dados íntegra.