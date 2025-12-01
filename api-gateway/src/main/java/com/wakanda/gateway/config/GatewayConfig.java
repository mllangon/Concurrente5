package com.wakanda.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayConfig implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Log para debugging
        System.out.println("Gateway Request: " + request.getMethod() + " " + request.getURI());
        System.out.println("Query Params: " + request.getQueryParams());
        
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Ejecutar antes que otros filtros
    }
}

