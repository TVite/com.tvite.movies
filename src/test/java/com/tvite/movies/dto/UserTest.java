package com.tvite.movies.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserTest {

    @Test
    public void testUser() {
        User user = new User("Peter");
        user.setFavorites(new ArrayList<Movie>());

        Assertions.assertEquals("Peter", user.getName());
        Assertions.assertTrue(user.getFavorites().size() == 0);

        user.setName("jOhN");
        Assertions.assertEquals("jOhN", user.getName());
    }
}
