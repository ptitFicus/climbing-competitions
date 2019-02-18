package io.ficus.climbingcompetitions.domain.port.secondary;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.model.CompetitionDetail;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompetitionStore {
    void saveCompetitions(List<Competition> competitions);
    void clear();
    void saveDetail(CompetitionDetail detail);
    Optional<CompetitionDetail> findDetail(String id);
    List<Competition> find(List<Competition.Category> categories,
                           Optional<LocalDate> begin,
                           Optional<LocalDate> end,
                           Optional<String> search);
}
