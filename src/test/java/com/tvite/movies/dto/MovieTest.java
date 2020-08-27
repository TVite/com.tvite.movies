package com.tvite.movies.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MovieTest {

    @Test
    public void testMovie() {
        Movie movie = new Movie("XYZ999");
        Assertions.assertEquals("XYZ999", movie.getId());
    }
}
