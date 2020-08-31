package com.tvite.movies.controllers;

import com.google.common.io.Resources;
import com.tvite.movies.dto.User;
import com.tvite.movies.services.MovieManager;
import com.tvite.movies.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
public class WebController {

    @Autowired
    private MovieManager searchService;

    @Autowired
    private UserManager userManager;

    @Value("${application.url}")
    private String appUrl;

    private User user;

    @GetMapping("/")
    public String index(@RequestParam(value = "user", required = false, defaultValue="Admin") String userName) throws IOException {
        user = userManager.findUser(userName);
        return transformIndexPage(
                getWebPageString("index.html")
        );
    }

    @GetMapping("/search")
    public String search(@RequestParam(name = "query") String query,
                         @RequestParam(value = "user", required = false, defaultValue="Admin") String userName) throws IOException {
        user = userManager.findUser(userName);
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
    private String transformAllPages(String page) throws IOException {

        List<String> userList = new ArrayList<>();
        for (User u: userManager.getUsers()) {
            userList.add("\"" + u.getName() + "\"");
        }

        return page
                .replace("${common.javascript}", Resources.toString(Resources.getResource("webapp/common.js"), StandardCharsets.UTF_8))
                .replace("${application.url}", appUrl)
                .replace("${user.name}", user.getName())
                .replace("${all.users}", userList.toString());
    }

    /**
     * Helper methods to replace placeholders for index page.
     * @param page Web page string
     * @return Transformed web page string
     */
    private String transformIndexPage(String page) {
        return page
                .replace("${favorites.list}", searchService.getMovies(user.getFavorites()).toString());
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
