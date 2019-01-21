package io.ficus.climbingcompetitions.domain.port.secondary;

import io.ficus.climbingcompetitions.domain.model.Competition;

import java.util.List;

public interface CompetitionStore {
    void saveCompetitions(List<Competition> competitions);
    void saveCompetition(Competition competition);
    void clear();
    List<Competition> findAll();
}
