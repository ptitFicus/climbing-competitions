package io.ficus.climbingcompetitions.api.controller;


import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.model.CompetitionDetail;
import io.ficus.climbingcompetitions.domain.port.primary.CompetitionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import javax.xml.catalog.Catalog;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Handler {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    private final CompetitionManager competitionManager;

    public Handler(CompetitionManager competitionManager) {
        this.competitionManager = competitionManager;
    }

    public Mono<ServerResponse> getCompetitions(ServerRequest request) {
        Optional<LocalDate> begin = request.attribute("begin").map(Object::toString).map(d -> LocalDate.parse(d, FORMATTER));
        Optional<LocalDate> end = request.attribute("end").map(Object::toString).map(d -> LocalDate.parse(d, FORMATTER));
        Optional<String> search = request.attribute("search").map(Object::toString);
        List<Competition.Category> categories = request.attribute("categories")
                .map(o -> (List<String>)o)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(Competition.Category::valueOf)
                .collect(Collectors.toList());

        return ServerResponse.ok().body(competitionManager.findCompetitions(categories, begin, end, search), Competition.class);
    }

    public Mono<ServerResponse> getCompetition(ServerRequest request) {
        String competitionId = String.valueOf(request.pathVariable("id"));

        return ServerResponse.ok().body(competitionManager.findDetail(competitionId), CompetitionDetail.class);
    }
}
