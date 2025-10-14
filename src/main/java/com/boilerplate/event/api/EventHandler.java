package com.boilerplate.event.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * WebFlux SSE 이벤트 핸들러.
 */
@Slf4j
@Component
public class EventHandler {

    /**
     * 전역 이벤트 스트림.
     */
    public Mono<ServerResponse> streamEvents(ServerRequest request) {
        log.info("전역 이벤트 스트림 연결");

        Flux<String> eventStream = Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "Event #" + sequence + " at " + LocalDateTime.now())
                .doOnCancel(() -> log.info("전역 이벤트 스트림 종료"));

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventStream, String.class);
    }

    /**
     * 사용자별 이벤트 스트림.
     */
    public Mono<ServerResponse> streamUserEvents(ServerRequest request) {
        String userId = request.pathVariable("userId");
        log.info("사용자별 이벤트 스트림 연결 - userId: {}", userId);

        Flux<String> eventStream = Flux.interval(Duration.ofSeconds(2))
                .map(sequence -> "User " + userId + " Event #" + sequence + " at " + LocalDateTime.now())
                .doOnCancel(() -> log.info("사용자 {} 이벤트 스트림 종료", userId));

        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(eventStream, String.class);
    }
}
