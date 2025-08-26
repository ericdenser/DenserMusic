package com.DenserMusic.DenserMusic.config;

import com.DenserMusic.DenserMusic.client.DeezerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {
    @Bean
    public DeezerClient deezerClient() {
        WebClient client = WebClient.builder()
                .baseUrl("https://api.deezer.com")
                .build();

        WebClientAdapter adapter = WebClientAdapter.create(client);

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(DeezerClient.class);
    }
}