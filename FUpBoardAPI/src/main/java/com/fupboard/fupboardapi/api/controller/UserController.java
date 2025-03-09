package com.fupboard.fupboardapi.api.controller;

//import the model you want to use
import com.fupboard.fupboardapi.api.model.User;
import com.fupboard.fupboardapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public User getUser(@RequestParam int id)
    {
        Optional user = userService.getUser(id);
         if (user.isPresent())
             return (User) user.get();

         //probs better to return some network error code or something, but just an example
         return null;
    }
}
