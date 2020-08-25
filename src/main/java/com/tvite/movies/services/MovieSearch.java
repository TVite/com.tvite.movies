package com.tvite.movies.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieSearch {

    @Value("${tmdb.api.key}")
    private String apiKey;

    public List<JSONObject> search(String query) {
        List<JSONObject> results = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        String queryResponse = restTemplate.getForObject("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" +query, String.class);

        try {
            JSONObject responseJson = new JSONObject(queryResponse);
            JSONArray resultsArray = (JSONArray) responseJson.get("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                results.add((JSONObject) resultsArray.get(i));
            }
        } catch (JSONException e) {
            results = new ArrayList<>();
        }

        return results;
    }

}
