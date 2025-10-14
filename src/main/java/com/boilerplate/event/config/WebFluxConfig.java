package com.boilerplate.event.config;

import com.boilerplate.event.api.EventHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * WebFlux 라우터 설정 클래스.
 */
@Configuration
public class WebFluxConfig {

    @Bean
    public RouterFunction<ServerResponse> eventRoutes(EventHandler eventHandler) {
        return route(GET("/events"), eventHandler::streamEvents)
                .andRoute(GET("/events/user/{userId}"), eventHandler::streamUserEvents);
    }
}
