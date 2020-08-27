package com.tvite.movies;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@SpringBootTest(classes = {MovieApplication.class, RestTemplate.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AppTest {

    @LocalServerPort
    private int webPort;

    @Value("${test.api.port}")
    private int apiPort;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Rule
    private WireMockServer apiServer;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    public void setup() {
        apiServer = new WireMockServer(apiPort);
        apiServer.start();
    }

    @AfterEach
    public void close() {
        apiServer.stop();
    }

    @Test
    public void testIndex() {
        String response = restTemplate.getForEntity("http://localhost:" + webPort, String.class).getBody();

        // Check if response is an html code
        Assertions.assertTrue(response.contains("<html lang=\"en\">") && response.contains("</html>"));
    }

    @Test
    public void testSearch() {
        apiServer.stubFor(get(urlPathEqualTo("/search"))
                .withQueryParam("api_key", equalTo(apiKey))
                .withQueryParam("query", equalTo("Jack"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody("{\"results\":[{title:\"FAKE_MOVIE_NAME\"}, {title:\"T_VITE\"}]}"))
        );

        String response = restTemplate.getForEntity("http://localhost:" + webPort + "/search?query=Jack", String.class).getBody();

        // Check if response contains a JavaScript variable with API results assigned to it
        Assertions.assertTrue(response.contains("const searchResults = [{\"title\":\"FAKE_MOVIE_NAME\"}, {\"title\":\"T_VITE\"}];"));
    }

    @Test
    public void testSearchInvalidFormat() {
        apiServer.stubFor(get(urlPathEqualTo("/search"))
                .withQueryParam("api_key", equalTo(apiKey))
                .withQueryParam("query", equalTo("Tom"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody("json expected but got wrong format"))
        );

        String response = restTemplate.getForEntity("http://localhost:" + webPort + "/search?query=Tom", String.class).getBody();

        // Check if api returns invalid json, results are set to an empty array
        Assertions.assertTrue(response.contains("const searchResults = [];"));
    }

}
