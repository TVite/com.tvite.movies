package com.tvite.movies.services;

import com.tvite.movies.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserManagerTest {

    @Test
    public void testDefaultUser() {
        UserManager manager = new UserManager();
        User defaultUser = manager.getUsers().get(0);

        Assertions.assertEquals(1, manager.getUsers().size());
        Assertions.assertEquals("Admin", defaultUser.getName());
        Assertions.assertEquals(0, defaultUser.getFavorites().size());
    }

    @Test
    public void testUserCreation() {
        UserManager manager = new UserManager();

        manager.newUser("TVITE");
        manager.newUser("AAABBB");

        Assertions.assertEquals(3, manager.getUsers().size());
        Assertions.assertEquals("TVITE", manager.getUsers().get(1).getName());
        Assertions.assertEquals("AAABBB", manager.getUsers().get(2).getName());
    }

    @Test
    public void testUserFind() {
        UserManager manager = new UserManager();

        User newUser = manager.newUser("testABC");
        User foundUser = manager.findUser("testABC");

        Assertions.assertTrue(newUser.equals(foundUser));
    }
}
