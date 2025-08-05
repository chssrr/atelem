package com.chssr.atelem.controllers;

import com.chssr.atelem.models.User;
import com.chssr.atelem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }
    @DeleteMapping
    public void deleteUser(Long id) {
        userRepository.deleteById(id);

    }
}
