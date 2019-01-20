package io.ficus.climbingcompetitions.domain.model;

import java.time.LocalDate;
import java.util.Set;

public class Competition {
    public final String name;
    public final LocalDate startDate;
    public final LocalDate endDate;
    public final Set<Category> categories;

    private Competition(Builder builder) {
        name = builder.name;
        startDate = builder.startDate;
        endDate = builder.endDate;
        categories = builder.categories;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Competition copy) {
        Builder builder = new Builder();
        builder.name = copy.name;
        builder.startDate = copy.startDate;
        builder.endDate = copy.endDate;
        builder.categories = copy.categories;
        return builder;
    }


    public static enum Category {
        INF8ANS,
        MICROBE,
        POUSSIN,
        BENJAMIN,
        MINIME,
        CADET,
        JUNIOR,
        SENIOR,
        VETERAN,
        UNKNOWN_CATEGORY
    }

    public static final class Builder {
        private String name;
        private LocalDate startDate;
        private LocalDate endDate;
        private Set<Category> categories;

        private Builder() {
        }

        public Builder withName(String val) {
            name = val;
            return this;
        }

        public Builder withStartDate(LocalDate val) {
            startDate = val;
            return this;
        }

        public Builder withEndDate(LocalDate val) {
            endDate = val;
            return this;
        }

        public Builder withCategories(Set<Category> val) {
            categories = val;
            return this;
        }

        public Competition build() {
            return new Competition(this);
        }
    }
}
