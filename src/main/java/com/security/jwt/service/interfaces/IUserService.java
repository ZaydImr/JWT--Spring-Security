package com.security.jwt.service.interfaces;

import com.security.jwt.entity.User;
import com.security.jwt.helpers.exception.EmailExistException;
import com.security.jwt.helpers.exception.UserNotFoundException;
import com.security.jwt.helpers.exception.UsernameExistException;

import javax.mail.MessagingException;
import java.util.List;

public interface IUserService {

    User register(String firstName, String lastName, String username, String email) throws UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

}
