package fr.ptitficus.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CompetitionClient {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final Map<String, Competition.Category> LETTER_CATEGORY = Map.of(
            "m8", Competition.Category.INF8ANS,
            "m", Competition.Category.MICROBE,
            "P", Competition.Category.POUSSIN,
            "B", Competition.Category.BENJAMIN,
            "M", Competition.Category.MINIME,
            "C", Competition.Category.CADET,
            "J", Competition.Category.JUNIOR,
            "S", Competition.Category.SENIOR,
            "V", Competition.Category.VETERAN
    );

    public Flux<Competition> getCompetitions() {
        return WebClient.create("https://www.ffme.fr/competition/calendrier-liste.html")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .map(Jsoup::parse)
                .map(this::extractCompetitions)
                .flatMapMany(Flux::fromIterable);
    }

    private Collection<Competition> extractCompetitions(Document document) {
        Elements trs = document.select(".infos_colonne tr");

        return StreamSupport.stream(trs.spliterator(), true)
                .map(this::extractCompetitionFromTr)
                .collect(Collectors.toList());
    }

    private Competition extractCompetitionFromTr(Element tr) {
        Element dateCell = tr.child(0);
        Element nameCell = tr.child(1);
        Element categoriesCell = tr.child(3);


        Competition comp = new Competition();
        comp.name = nameCell.text();

        String[] dates = dateCell.text().split(" ");

        LocalDate startDate = LocalDate.parse(dates[0], FORMATTER);
        comp.startDate = startDate;

        if(dates.length > 1) {
            comp.endDate = LocalDate.parse(dates[1], FORMATTER);
        } else {
            comp.endDate = startDate;
        }

        Set<Competition.Category> categories = Arrays.stream(categoriesCell.text().split(" "))
                .skip(1)
                .map(letter -> LETTER_CATEGORY.getOrDefault(letter, Competition.Category.UNKNOWN_CATEGORY))
                .collect(Collectors.toSet());

        comp.categories = categories;

        return comp;
    }
}
