package com.hoomicorp.security.provider;

import com.hoomicorp.model.dto.UserInfoDto;
import com.hoomicorp.security.UsernameAndPasswordAuthentication;
import com.hoomicorp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BasicAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;

    @Autowired
    public BasicAuthenticationProvider(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {


        final String username = authentication.getName();
        final String password = (String) authentication.getCredentials();

        final UserInfoDto login = authService.login(username, password);
        ((UsernameAndPasswordAuthentication) authentication).setId(login.getId());

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authClass) {
        return authClass.equals(UsernameAndPasswordAuthentication.class);
    }
}
