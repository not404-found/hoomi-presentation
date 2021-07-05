package com.hoomicorp.security.provider;

import com.hoomicorp.exception.AuthenticationException;
import com.hoomicorp.security.TokenAuthentication;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FirebaseAuthenticationProvider implements AuthenticationProvider {

    private final FirebaseTokenProvider firebaseTokenProvider;

    public FirebaseAuthenticationProvider(FirebaseTokenProvider firebaseTokenProvider) {
        this.firebaseTokenProvider = firebaseTokenProvider;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {

        final TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        final String token = (String) tokenAuthentication.getPrincipal();

        if (Objects.isNull(token)) {
            throw new AuthenticationException("Token must not be null");
        }

        boolean isValid = firebaseTokenProvider.validateToken(token);
        if (!isValid) {
            throw new AuthenticationException("Token is expired or invalid");
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authClass) {
        return authClass.equals(TokenAuthentication.class);
    }
}
