package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); // exchange로부터 request, response 값을 얻는다
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global Filter baseMessage : {}", config.getBaseMessage());

            if(config.isPreLogger()) {
             log.info("Global Filter Start: request id -> {}", request.getId());
            }

            // Custom Post Filter
            // post filter를 적용하기 위해서 반환시켜주는 chain에 연결시켜서
            return chain.filter(exchange).then(Mono.fromRunnable(() -> { // Mono : spring5에서 추가되었음
                if(config.isPostLogger()) {
                    log.info("Global Filter END: response code -> {}", response.getStatusCode());
                }
            }));
        });
    }

    @Data
    public static class Config {
        // put the configuration properties
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
