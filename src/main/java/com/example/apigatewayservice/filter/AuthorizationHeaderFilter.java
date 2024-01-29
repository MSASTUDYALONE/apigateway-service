package com.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class); // Config 정보를 부모 클래스에 전달해줘야 한다.
        this.env = env;
    }

    // 설정에 관련되어 있는 작업 전담
    public static class Config {

    }

//    login -> token -> user(with token) -> header(include token)
    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest(); // step1. 토큰 받기

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED); //onError(반환값,메시지,HttpStatus)
            }

            //authorizationHeader에는 Bearer토큰 저장
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0); // 앞의 get의 반환값이 List이기 때문에 0번째 값을 가져오겠다.(get(0))
            String jwt = authorizationHeader.replace("Bearer", ""); // "Bearer"를 제외한게 token

            if (!isJwtValid(jwt)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange); // step2. 통과 메시지
        });
    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret")) // 토큰 암호화한거 풀기
                    .parseClaimsJws(jwt).getBody() // 복호화 대상 : jwt, parseClaimsJws: 토큰을 문자형 데이터값으로 파싱
                    .getSubject(); // 그중에서 subject값만 가져옴

            return returnValue;
        } catch (Exception e) { // 파싱하다 오류날 수도 있음
            returnValue = false;
        }

//        if (subject == null || subject.isEmpty()) {
        if (subject == null ) {
            returnValue = false;
        }

        return returnValue;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse(); // ServletResponse 아님
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }
}