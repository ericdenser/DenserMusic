# DenserMusic API

Uma API RESTful para um aplicativo de música, construída com Java 21, Spring Boot 3 e PostgreSQL.

---

## Funcionalidades (Features)

* **Gerenciamento de Usuários:** CRUD completo para usuários, incluindo listagem e obtenção por ID.
* **Gerenciamento de Músicas (Tracks):** CRUD completo para músicas.
* **Gerenciamento de Playlists:** CRUD completo para playlists, permitindo que usuários criem e gerenciem suas próprias listas de músicas.
* **Mapeamento de Objetos:** Uso de MapStruct para um mapeamento limpo e eficiente entre entidades JPA (Models) e Objetos de Transferência de Dados (DTOs).

* **Próxima Implementação::** Autenticação e Autorização: Sistema completo de registr e login usando Spring Security e tokens JWT.

## Testes e Qualidade de Código

O projeto utiliza JUnit 5 e Mockito para testes unitários e de integração, com relatórios de cobertura gerados pelo JaCoCo.

### Cobertura de Código (JaCoCo)

Este é o último relatório de cobertura de testes do projeto:

![Relatório de Cobertura JaCoCo](/src/main/java/com/densermusic/densermusic/docs/TesteJacoco.png)
