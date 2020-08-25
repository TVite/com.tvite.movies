package com.tvite.movies;

import com.google.common.io.Resources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
public class WebController {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @GetMapping("/")
    public String index() {
        return getWebPage("index.html");
    }

    /**
     * Helper method to read the content of a file as a string.
     * @param filename Path and the name of the file in resources/webapp.
     * @return Content of files as a string
     */
    private String getWebPage(String filename) {
        String fileContent;
        try {
            fileContent = Resources.toString(Resources.getResource("webapp/" + filename), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException | IOException e) {
            fileContent = "Could not find " + filename;
        }
        return replacePlaceholders(fileContent);
    }

    private String replacePlaceholders(String string) {
        return string.replace("${tmdb.api.key}", apiKey);
    }
}
