package io.ficus.climbingcompetitions.api.controller;


import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.port.primary.CompetitionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
public class Handler {
    private final CompetitionManager competitionManager;

    public Handler(CompetitionManager competitionManager) {
        this.competitionManager = competitionManager;
    }

    public Mono<ServerResponse> handleCompetitonsRequest(ServerRequest request) {
        return ServerResponse.ok().body(competitionManager.findCompetitions(), Competition.class);
    }
}
