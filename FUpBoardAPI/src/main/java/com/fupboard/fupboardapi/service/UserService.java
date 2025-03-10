package com.fupboard.fupboardapi.service;

import com.fupboard.fupboardapi.api.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    //Here we would make some db call but just an example
    private List<User> userList;

    public UserService() {
        userList = new ArrayList<>();

        User user1 = new User(1,"User1", 22, "user1@test.com");
        User user2 = new User(2,"User2", 33, "user2@test.com");
        User user3 = new User(3,"User3", 44, "user3@test.com");
        User user4 = new User(4,"User4", 55, "user4@test.com");

        userList.addAll(Arrays.asList(user1, user2, user3, user4));
    }

    public Optional getUser(Integer id)
    {
        Optional optional = Optional.empty();
        for (User user: userList) {
            if (id.equals(user.getId()))
            {
                optional = Optional.of(user);
                return optional;
            }
        }

        return optional;
    }
}
