package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); // exchange로부터 request, response 값을 얻는다
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter : request id -> {}",request.getId());

            // Custom Post Filter
            // post filter를 적용하기 위해서 반환시켜주는 chain에 연결시켜서
            return chain.filter(exchange).then(Mono.fromRunnable(() -> { // Mono : spring5에서 추가되었음
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());
                }));
        });
    }

    public static class Config {
        // put the configuration properties

    }
}
