package fr.ptitficus.demo;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class Handler {
    public Mono<ServerResponse> handleHelloRequest(ServerRequest request) {
        return ServerResponse.ok().body(Mono.just("Hello World !"), String.class);
    }
}
