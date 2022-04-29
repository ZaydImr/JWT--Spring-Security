package com.security.jwt.helpers;

import com.security.jwt.entity.User;
import com.security.jwt.security.UserPrincipal;
import com.security.jwt.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

public class AuthenticationSuccessListener {

    @Autowired
    private LoginAttemptService loginAttemptService;

    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
