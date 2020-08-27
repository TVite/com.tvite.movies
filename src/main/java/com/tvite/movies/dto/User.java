package com.tvite.movies.dto;

import java.util.List;

public class User {

    private String name;

    private List<Movie> favorites;

    public String getName() {
        return this.name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public List<Movie> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<Movie> pFavorites) {
        favorites = pFavorites;
    }
}
