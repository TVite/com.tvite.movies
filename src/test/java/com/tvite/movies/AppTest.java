package com.tvite.movies;

import org.apache.log4j.Logger;

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

    private Logger log = Logger.getLogger(AppTest.class.getName());

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
        apiServer.stubFor(get(urlPathEqualTo("/api/search"))
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
    public void testUserFavorites() {
        String userName = "NewUserTest";
        String favMovieId = "98765";
        String favMovieTitle = "Test Favorite Movie";
        String favMovieData = "{\"id\":" + favMovieId + ",\"title\":\"" + favMovieTitle + "\",\"poster_path\":\"poster.jpg\"}";

        apiServer.stubFor(get(urlPathEqualTo("/api/movie/" + favMovieId))
                .withQueryParam("api_key", equalTo(apiKey))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody(favMovieData))
        );

        restTemplate.getForEntity("http://localhost:" + webPort + "/user/create?user=" + userName, String.class);
        restTemplate.getForEntity("http://localhost:" + webPort + "/user/favorite?user=" + userName + "&movie_id=" + favMovieId, String.class);
        String response = restTemplate.getForEntity("http://localhost:" + webPort + "/?user=" + userName, String.class).getBody();

        Assertions.assertTrue(response.contains("allUsers = [\"Admin\", \"NewUserTest\"]"));
        Assertions.assertTrue(response.contains("favoritesList = [{\"id\":98765,\"title\":\"Test Favorite Movie\",\"poster_path\":\"poster.jpg\"}]"));
    }

    @Test
    public void testInvalidFormatResponse() {
        apiServer.stubFor(get(urlPathEqualTo("/api/search"))
                .withQueryParam("api_key", equalTo(apiKey))
                .withQueryParam("query", equalTo("Tom"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody("invalid format"))
        );

        String response = restTemplate.getForEntity("http://localhost:" + webPort + "/search?query=Tom", String.class).getBody();

        // Check if api returns invalid json, results are set to an empty array
        Assertions.assertTrue(response.contains("searchResults = [];"));

        apiServer.stubFor(get(urlPathEqualTo("/api/movie/123"))
                .withQueryParam("api_key", equalTo(apiKey))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withStatus(200)
                        .withBody("not json"))
        );

        restTemplate.getForEntity("http://localhost:" + webPort + "/user/favorite?user=Admin&movie_id=123", String.class);
        String response2 = restTemplate.getForEntity("http://localhost:" + webPort + "/?user=Admin", String.class).getBody();

        Assertions.assertTrue(response2.contains("favoritesList = [null]"));
    }
}
