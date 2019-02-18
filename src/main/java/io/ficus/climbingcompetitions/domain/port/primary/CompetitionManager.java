package io.ficus.climbingcompetitions.domain.port.primary;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.model.CompetitionDetail;
import io.ficus.climbingcompetitions.domain.port.secondary.CompetitionStore;
import io.ficus.climbingcompetitions.infrastructure.client.CompetitionClient;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class CompetitionManager {
    private final CompetitionClient client;
    private final CompetitionStore store;

    public CompetitionManager(CompetitionClient client, CompetitionStore store) {
        this.client = client;
        this.store = store;
    }

    @Scheduled(fixedRate = 3600000)
    public void init() {
        reloadCompetitions();
    }

    public Disposable reloadCompetitions() {
        return client.getCompetitions().collectList().subscribe(competitions -> {
            store.clear();
            store.saveCompetitions(competitions);
        });
    }

    public Flux<Competition> findCompetitions(
            List<Competition.Category> categories,
            Optional<LocalDate> begin,
            Optional<LocalDate> end,
            Optional<String> search) {
        return Flux.fromIterable(store.find(categories, begin, end, search));
    }

    public Mono<CompetitionDetail> findDetail(String id) {
        return store.findDetail(id)
                .map(Mono::just)
                .orElseGet(() -> {
                    Mono<CompetitionDetail> detail = CompetitionClient.getDetail(id);
                    detail.subscribe(store::saveDetail);
                    return detail;
                });
    }
}
