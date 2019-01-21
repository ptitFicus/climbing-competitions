package io.ficus.climbingcompetitions;

import io.ficus.climbingcompetitions.domain.port.primary.CompetitionManager;
import io.ficus.climbingcompetitions.domain.port.secondary.CompetitionStore;
import io.ficus.climbingcompetitions.infrastructure.client.CompetitionClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompetitionConfiguration {
    @Bean
    CompetitionManager competitionManager(CompetitionStore store, CompetitionClient client) {
        return new CompetitionManager(client, store);
    }
}
