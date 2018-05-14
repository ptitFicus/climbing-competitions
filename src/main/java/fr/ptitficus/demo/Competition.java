package fr.ptitficus.demo;

import java.time.LocalDate;
import java.util.Set;

public class Competition {
    public String name;
    public LocalDate startDate;
    public LocalDate endDate;
    public Set<Category> categories;


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
}
