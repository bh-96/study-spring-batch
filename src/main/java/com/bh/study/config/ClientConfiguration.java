package com.bh.study.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory clientHttpRequestFactory
                = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setReadTimeout(10 * 1000);
        clientHttpRequestFactory.setConnectTimeout(10 * 1000);
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
