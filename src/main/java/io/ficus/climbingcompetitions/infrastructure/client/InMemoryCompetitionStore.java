package io.ficus.climbingcompetitions.infrastructure.client;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.port.secondary.CompetitionStore;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryCompetitionStore implements CompetitionStore {
    private List<Competition> competitions = new ArrayList<>();

    @Override
    public void saveCompetitions(List<Competition> competitions) {
        this.competitions.addAll(competitions);
    }

    @Override
    public void saveCompetition(Competition competition) {
        this.competitions.add(competition);
    }

    @Override
    public void clear() {
        competitions.clear();
    }

    @Override
    public List<Competition> findAll() {
        return new ArrayList<>(competitions);
    }
}
