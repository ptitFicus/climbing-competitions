package io.ficus.climbingcompetitions.api.controller;


import io.ficus.climbingcompetitions.client.CompetitionClient;
import io.ficus.climbingcompetitions.domain.model.Competition;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class Handler {
    private final CompetitionClient competitionClient;

    public Handler(CompetitionClient competitionClient) {
        this.competitionClient = competitionClient;
    }

    public Mono<ServerResponse> handleCompetitonsRequest(ServerRequest request) {
        return ServerResponse.ok().body(competitionClient.getCompetitions(), Competition.class);
    }
}
