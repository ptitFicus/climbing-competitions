package io.ficus.climbingcompetitions;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class CompetitionClientTest {
    @Test
    public void testMaxPageParsing() throws IOException {
        String fileContent = Files.readString(Path.of("src/test/resources/mocks/firstResult.html"));

        Document document = Jsoup.parse(fileContent);
        assertThat(CompetitionClient.extractMaxPage(document)).isEqualTo(6);
    }
}
