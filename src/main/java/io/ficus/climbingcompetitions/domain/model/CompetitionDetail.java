package io.ficus.climbingcompetitions.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompetitionDetail {
    public final LocalDateTime startTime;
    public final String municipality;
    public final String place;
    public final Integer placeCount;
    public final String detail;
    public final LocalDate inscriptionLimit;

    private CompetitionDetail(Builder builder) {
        startTime = builder.startTime;
        municipality = builder.municipality;
        place = builder.place;
        placeCount = builder.placeCount;
        detail = builder.detail;
        inscriptionLimit = builder.inscriptionLimit;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(CompetitionDetail copy) {
        Builder builder = new Builder();
        builder.startTime = copy.startTime;
        builder.municipality = copy.municipality;
        builder.place = copy.place;
        builder.placeCount = copy.placeCount;
        builder.detail = copy.detail;
        builder.inscriptionLimit = copy.inscriptionLimit;
        return builder;
    }


    public static final class Builder {
        private LocalDateTime startTime;
        private String municipality;
        private String place;
        private Integer placeCount;
        private String detail;
        private LocalDate inscriptionLimit;

        private Builder() {
        }

        public Builder withStartTime(LocalDateTime val) {
            startTime = val;
            return this;
        }

        public Builder withMunicipality(String val) {
            municipality = val;
            return this;
        }

        public Builder withPlace(String val) {
            place = val;
            return this;
        }

        public Builder withPlaceCount(Integer val) {
            placeCount = val;
            return this;
        }

        public Builder withDetail(String val) {
            detail = val;
            return this;
        }

        public Builder withInscriptionLimit(LocalDate val) {
            inscriptionLimit = val;
            return this;
        }

        public CompetitionDetail build() {
            return new CompetitionDetail(this);
        }
    }
}
