package io.ficus.climbingcompetitions.infrastructure.adapter.secondary;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.port.secondary.CompetitionStore;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class InMemoryCompetitionStore implements CompetitionStore {
    private List<Competition> competitions = new ArrayList<>();

    @Override
    public void saveCompetitions(List<Competition> competitions) {
        this.competitions.addAll(competitions);
    }

    @Override
    public void clear() {
        competitions.clear();
    }

    @Override
    public List<Competition> find(List<Competition.Category> categories,
                                  Optional<LocalDate> begin,
                                  Optional<LocalDate> end,
                                  Optional<String> search) {
        List<Predicate<Competition>> predicates = filterFrom(categories, begin, end, search);

        return competitions.stream()
                .filter(competition -> predicates.isEmpty() || predicates.stream().anyMatch(p -> p.test(competition)))
                .collect(Collectors.toList());
    }

    private List<Predicate<Competition>> filterFrom(List<Competition.Category> categories,
                                                    Optional<LocalDate> begin,
                                                    Optional<LocalDate> end,
                                                    Optional<String> search) {
        List<Predicate<Competition>> predicates = new ArrayList<>();
        if(!categories.isEmpty()) {
            categories.forEach(
                    category -> predicates.add((Competition competition) -> competition.categories.contains(category))
            );
        }

        begin.ifPresent(bd -> predicates.add(competation -> competation.startDate.compareTo(bd) >= 0));
        end.ifPresent(ed  -> predicates.add(competation -> competation.startDate.compareTo(ed) <= 0));
        search.ifPresent(s -> predicates.add(competition -> competition.name.contains(s)));

        return predicates;
    }
}
