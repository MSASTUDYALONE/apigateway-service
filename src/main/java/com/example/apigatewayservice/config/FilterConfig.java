package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

//@Configuration
public class FilterConfig {
//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // first-service 등록
                .route(r -> r.path("/first-service/**") // #1 r값이 전달되면 path를 확인하고
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header") // #2 필터를 적용해서
                                .addResponseHeader("first-response", "first-response-header")) // chaining 함수
                        .uri("http://localhost:8081")) // #3 uri로 이동시켜준다.

                // second-service 등록
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}
