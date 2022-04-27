package com.security.jwt.web;

import com.security.jwt.entity.User;
import com.security.jwt.helpers.exception.EmailExistException;
import com.security.jwt.helpers.exception.ExceptionHandling;
import com.security.jwt.helpers.exception.UserNotFoundException;
import com.security.jwt.helpers.exception.UsernameExistException;
import com.security.jwt.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/", "user"})
public class UserController extends ExceptionHandling {

    @Autowired
    private IUserService userService;

    @PostMapping("register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, EmailExistException, UsernameExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    @GetMapping("login")
    public String loginUser(){
        return "Test";
    }

}
