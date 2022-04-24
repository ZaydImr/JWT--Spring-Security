package com.security.jwt.web;

import com.security.jwt.helpers.exception.EmailExistException;
import com.security.jwt.helpers.exception.ExceptionHandling;
import com.security.jwt.helpers.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/", "user"})
public class UserController extends ExceptionHandling {

    @GetMapping("home")
    public String showUser() throws  UserNotFoundException{
        throw new UserNotFoundException("This email address is already taken");
    }

    @GetMapping("login")
    public String loginUser(){
        return "Test";
    }

}
