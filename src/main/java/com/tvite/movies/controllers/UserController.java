package com.tvite.movies.controllers;

import com.tvite.movies.dto.Movie;
import com.tvite.movies.dto.User;
import com.tvite.movies.services.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserManager manager;

    @GetMapping("/user/create")
    public String newUser(@RequestParam(name = "user") String userName) {
        User user  = manager.newUser(userName);
        return user != null ? "Created a new user [ " + userName + " ]." : "Error creating user [ " + userName + " ].";
    }

    @GetMapping("/user/favorite")
    public String addToFavorites(@RequestParam(name = "movie_id") String movieId,
                                 @RequestParam(name = "user") String userName) {
        manager.findUser(userName).getFavorites().add(new Movie(movieId));
        return "Added movie [ " + movieId + " ] for user [ " + userName + " ].";
    }
}
