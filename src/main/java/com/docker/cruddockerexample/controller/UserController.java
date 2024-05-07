package com.docker.cruddockerexample.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docker.cruddockerexample.entity.User;
import com.docker.cruddockerexample.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(value = "/save")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        Optional<User> existsUser = userService.existsByEmail(user.getEmail());
        
        if (existsUser.isPresent()) {
            LOGGER.info("Email already exists.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        User newUser = userService.saveUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id).get();
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<User>> listUsers() {
        try {
            List<User> users = userService.listAll();
            if (users.isEmpty()) {
                LOGGER.info("Empty user list.");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(users, HttpStatus.OK);
            }
        } catch (Exception e) {
            LOGGER.error("An error occurred while getting the list of users.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") int id, @RequestBody User user) {
        User userUpdated = userService.getUserById(id).get();
        userUpdated.setId(user.getId());
        userUpdated.setNombre(user.getNombre());
        userUpdated.setEmail(user.getEmail());

        User currentUser = userService.saveUser(userUpdated);
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        boolean ok = userService.deleteUserById(id);
        if (ok) {
            return "User with id: " + id + " was deleted successfully";
        } else {
            return "User with id: " + id + " not found";
        }
    }
}