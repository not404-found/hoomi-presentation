package com.hoomicorp.security.provider;

import com.hoomicorp.security.TokenAuthentication;
import com.hoomicorp.exception.AuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {
    private final JWTTokenProvider tokenProvider;

    public JWTAuthenticationProvider(JWTTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {

        final TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        final String token = (String) tokenAuthentication.getPrincipal();

        if (Objects.isNull(token)) {
            throw new AuthenticationException("JWT token must not be null");
        }

        boolean isValid = tokenProvider.validateToken(token);
        if (!isValid) {
            throw new AuthenticationException("JWT token is expired or invalid");
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authClass) {
        return authClass.equals(TokenAuthentication.class);
    }
}

