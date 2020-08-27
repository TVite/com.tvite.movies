package com.tvite.movies.controllers;

import com.google.common.io.Resources;
import com.tvite.movies.services.MovieSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class WebController {

    @Autowired
    private MovieSearch searchService;

    @Value("${application.url}")
    private String appUrl;

    @GetMapping("/")
    public String index() throws IOException {
        return getWebPageString("index.html");
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "query") String query) throws IOException {
        return transformSearchPage(
                getWebPageString("search.html"),
                query
        );
    }

    /**
     * Helper method to read the content of a file as a string.
     * @param filename Path and the name of the file in resources/webapp.
     * @return Content of files as a string
     */
    private String getWebPageString(String filename) throws IOException {
        return transformAllPages(Resources.toString(Resources.getResource("webapp/" + filename), StandardCharsets.UTF_8));
    }

    /**
     * Helper method to replace placeholders for all pages.
     * @param page Web page string
     * @return Transformed web page string
     */
    private String transformAllPages(String page) {
        return page
                .replace("${application.url}", appUrl);
    }

    /**
     * Helper methods to replace placeholders for search results page.
     * @param page Web page string
     * @param query Search query
     * @return Transformed web page string
     */
    private String transformSearchPage(String page, String query) {
        return page
                .replace("${search.results}", searchService.search(query).toString())
                .replace("${search.query}", query);
    }
}
