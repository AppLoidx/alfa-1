package com.apploidx.alfa1.http;

import lombok.SneakyThrows;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author Arthur Kupriyanov on 27.06.2020
 */
@Configuration
public class RestTemplateConfig {
    @Value("${alfabank.key}")
    private String key;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @SneakyThrows
    @Bean
    public RestTemplate restTemplate() {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = "password".toCharArray();
            InputStream jksInputStream = RestTemplateConfig.class.getClassLoader().getResourceAsStream("alfabattle.jks");
            keyStore.load(jksInputStream, password);
            SSLContext sslContext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, password)
                    .build();
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLContext(sslContext)
                    .build();
            var requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            return new RestTemplate(requestFactory);

    }
}
