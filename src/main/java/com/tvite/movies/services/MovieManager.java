package com.tvite.movies.services;

import com.tvite.movies.dto.Movie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class MovieManager {

    @Value("${tmdb.movie.search.url}")
    private String searchUrl;

    @Value("${tmdb.api.key}")
    private String apiKey;

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * Search for movies based on title.
     * @param query Search query.
     * @return List of JSON Objects that represent data on a movie.
     */
    public List<JSONObject> search(String query) {

        List<JSONObject> results = new ArrayList<>();
        String queryUrl = searchUrl + "/search/movie?api_key=" + apiKey + "&query=" + query;

        try {
            JSONObject queryResponse = new JSONObject(restTemplate.getForObject(queryUrl, String.class));
            JSONArray resultsArray = (JSONArray) queryResponse.get("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                results.add((JSONObject) resultsArray.get(i));
            }
        } catch (JSONException e) {
            results = new ArrayList<>();
        }

        return results;
    }

    /**
     * Search for a movie based on ID.
     * @param id Movie ID
     * @return Movie data
     */
    public JSONObject getMovie(String id) {
        JSONObject result;
        String queryUrl = searchUrl + "/movie/" + id + "?api_key=" + apiKey;
        try {
            result = new JSONObject(restTemplate.getForObject(queryUrl, String.class));
        } catch (JSONException e) {
            result = null;
        }
        return result;
    }

    /**
     * Search for movies based on a list of movie DTOs.
     * @param movies List of Movie DTOs.
     * @return List of JSON Objects that represent data on a movie.
     */
    public List<JSONObject> getMovies(List<Movie> movies) {
        List<JSONObject> results = new ArrayList<>();
        for (Movie movie: movies) {
            results.add( getMovie(movie.getId()) );
        }
        return results;
    }
}
