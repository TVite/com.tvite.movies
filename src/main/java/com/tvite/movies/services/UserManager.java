package com.tvite.movies.services;

import com.tvite.movies.dto.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserManager {

    List<User> users = new ArrayList<>();

    public UserManager() {
        users.add(new User("Admin"));
    }

    /**
     * Get all users.
     * @return User list.
     */
    public List<User> getUsers() {
        return this.users;
    }

    /**
     * Create a new user. If name is taken, return null.
     * @param name Name of the user.
     * @return The new user.
     */
    public User newUser(String name) {
        User user = null;
        if (findUser(name) == null && name.matches("[a-zA-Z0-9]*")) {
            user = new User(name);
            users.add(user);
        }
        return user;
    }

    /**
     * Find a user in list of all users. If user does not exist, return null.
     * @param name Name of the user.
     * @return User.
     */
    public User findUser(String name) {
        User result = null;
        for (User user: users) {
            if (user.getName().equals(name)) {
                result = user;
            }
        }
        return result;
    }
}
