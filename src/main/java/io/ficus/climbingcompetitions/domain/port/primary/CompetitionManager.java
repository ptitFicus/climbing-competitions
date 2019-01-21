package io.ficus.climbingcompetitions.domain.port.primary;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.port.secondary.CompetitionStore;
import io.ficus.climbingcompetitions.infrastructure.client.CompetitionClient;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

public class CompetitionManager {
    private final CompetitionClient client;
    private final CompetitionStore store;

    public CompetitionManager(CompetitionClient client, CompetitionStore store) {
        this.client = client;
        this.store = store;
    }

    @PostConstruct
    @Scheduled(fixedRate = 3600000)
    public void init() {
        reloadCompetitions();
    }

    public Disposable reloadCompetitions() {
        store.clear();
        return client.getCompetitions().collectList().subscribe(store::saveCompetitions);
    }

    public Flux<Competition> findCompetitions() {
        return Flux.fromIterable(store.findAll());
    }
}
