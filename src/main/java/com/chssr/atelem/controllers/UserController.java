package com.chssr.atelem.controllers;

import com.chssr.atelem.models.User;
import com.chssr.atelem.repositories.UserRepository;
import com.chssr.atelem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public List<User> allUsers() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userService.save(user);
    }
    @DeleteMapping
    public void deleteUser(@RequestBody Long id) {
        userService.deleteById(id);

    }
}
