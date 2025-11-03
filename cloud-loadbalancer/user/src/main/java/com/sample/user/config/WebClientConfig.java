package com.sample.user.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(HaloWorldClientProperties.class)
@LoadBalancerClient(name = "halo-world", configuration = HaloWorldConfiguration.class)
public class WebClientConfig {

    @Bean
    @LoadBalanced /*annotated on Builder only*/
    WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient haloWorldWebClient(WebClient.Builder lbBuilder, HaloWorldClientProperties properties) {
        return lbBuilder.baseUrl("http://" + properties.getServiceId()).build();
    }
}