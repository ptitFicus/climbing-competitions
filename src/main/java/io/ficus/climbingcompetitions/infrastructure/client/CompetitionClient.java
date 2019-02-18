package io.ficus.climbingcompetitions.infrastructure.client;

import io.ficus.climbingcompetitions.domain.model.Competition;
import io.ficus.climbingcompetitions.domain.model.CompetitionDetail;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CompetitionClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompetitionClient.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final DateTimeFormatter DETAIL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
        return WebClient.create("https://www.ffme.fr/competition/calendrier-liste.html?DISCIPLINE=ESC&CPT_FUTUR=1")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .map(Jsoup::parse)
                .map(document -> Tuples.of(extractCompetitions(document), extractMaxPage(document)))
                .flux()
                .flatMap(tuple -> Flux.concat(Flux.fromIterable(tuple.getT1()), getRemainingCompetitions(tuple.getT2())));
    }

    private Flux<Competition> getRemainingCompetitions(Integer count) {
        // Skip first, index starts at 1
        return Flux.range(2, count-1)
                .flatMap(index -> {
                    String url = "https://www.ffme.fr/competition/calendrier-liste.html?DISCIPLINE=ESC&CPT_FUTUR=1&page=" + index;
                    LOGGER.debug("Fetching {}", url);
                    return WebClient.create(url)
                            .get()
                            .retrieve()
                            .bodyToMono(String.class)
                            .flux();
                })
                .map(Jsoup::parse)
                .map(this::extractCompetitions)
                .flatMap(Flux::fromIterable);
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


        Competition.Builder comp = Competition.newBuilder();
        comp.withName(nameCell.text());

        String url = nameCell.getElementsByTag("a").attr("href");
        String id = url.substring(url.lastIndexOf("/") + 1).split("\\.")[0];
        comp.withId(id);


        String[] dates = dateCell.text().split(" ");

        LocalDate startDate = LocalDate.parse(dates[0], FORMATTER);
        comp.withStartDate(startDate);

        if (dates.length > 1) {
            comp.withEndDate(LocalDate.parse(dates[1], FORMATTER));
        } else {
            comp.withEndDate(startDate);
        }

        Set<Competition.Category> categories = Arrays.stream(categoriesCell.text().split(" "))
                .skip(1)
                .map(letter -> LETTER_CATEGORY.getOrDefault(letter, Competition.Category.UNKNOWN_CATEGORY))
                .collect(Collectors.toSet());

        comp.withCategories(categories);

        return comp.build();
    }

    static Integer extractMaxPage(Document document) {
        Elements links = document.select("#num_pages_box > a");
        Node lastPageLink = links.last();
        String lastPageUrl = lastPageLink.attr("href");
        String lastPageNumber = lastPageUrl.split("page=")[1];
        return Integer.valueOf(lastPageNumber);
    }

    public static Mono<CompetitionDetail> getDetail(String id) {
        return WebClient.create("https://www.ffme.fr/competition/fiche/" + id + ".html")
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .map(Jsoup::parse)
                .map(CompetitionClient::extractDetail);
    }

    static CompetitionDetail extractDetail(Document document) {
        Element generalInformationFieldset = document.getElementsByTag("fieldset").first();

        CompetitionDetail.Builder detailBuilder = CompetitionDetail.newBuilder();
        extractInformation(generalInformationFieldset, "Date")
                .map(dateStr -> LocalDate.parse(dateStr ,DETAIL_FORMATTER))
                .flatMap(date -> extractInformation(generalInformationFieldset, "Heure d'accueil")
                        .map(String::toUpperCase)
                        .map(startTime -> startTime.split("H"))
                        .map(splitted -> {
                            if(splitted.length < 2) {
                                return new String[]{splitted[0], "00"};
                            }

                            return splitted;
                        })
                        .map(splittedStartTime -> date.atTime(
                                Integer.parseInt(splittedStartTime[0]),
                                Integer.parseInt(splittedStartTime[1])
                            )
                        ))
                .ifPresent(detailBuilder::withStartTime);

        extractInformation(generalInformationFieldset, "commune").ifPresent(detailBuilder::withMunicipality);
        extractInformation(generalInformationFieldset, "lieu").ifPresent(detailBuilder::withPlace);
        extractInformation(generalInformationFieldset, "Nombre de places")
                .map(Integer::valueOf)
                .ifPresent(detailBuilder::withPlaceCount);

        document.getElementsByTag("fieldset")
                .stream()
                .filter(fieldset -> {
                    Element element = fieldset.getElementsByTag("legend").first();
                    return element.text().toUpperCase().contains("INSCRIPTION");
                })
                .findAny()
                .flatMap(fieldset ->extractInformation(fieldset, "Date limite d'inscription"))
                .map(dateStr -> LocalDate.parse(dateStr, DETAIL_FORMATTER))
                .ifPresent(detailBuilder::withInscriptionLimit);

        return detailBuilder.build();
    }

    static Optional<String> extractInformation(Element fieldset, String label) {
        String search = label.toUpperCase() + " :";
        return fieldset.getElementsByTag("p")
            .stream()
            .map(Element::text)
            .filter(strContent -> strContent.toUpperCase().contains(search))
            .findAny()
            .map(strContent -> strContent.split(":")[1])
            .map(String::trim);
    }
}
