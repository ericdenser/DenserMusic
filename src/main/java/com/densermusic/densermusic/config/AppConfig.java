package com.densermusic.densermusic.config;

import com.densermusic.densermusic.client.DeezerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.beans.factory.annotation.Value; // Importe esta classe

@Configuration
public class AppConfig {

    //configurações devem sempre estar fora do código, em arquivos de propriedades.
    @Value("${deezer.api.base-url}") // injeta o valor da propriedade
    private String deezerApiBaseUrl;


    @Bean
    public DeezerClient deezerClient() {
        WebClient client = WebClient.builder()
                .baseUrl(deezerApiBaseUrl) // usa a variável injetada
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(DeezerClient.class);
    }
}