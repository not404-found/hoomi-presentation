package com.hoomicorp.security.provider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Component;

@Component
public class FirebaseTokenProvider {

    private final FirebaseAuth firebaseAuth;

    public FirebaseTokenProvider(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    public String createToken(final String id) {
        try {
            return firebaseAuth.createCustomToken(id);
        } catch (FirebaseAuthException e) {
            //todo handle exception
            throw new RuntimeException();
        }
    }

    public boolean validateToken(final String token) {
        try {
            firebaseAuth.verifyIdToken(token);
        } catch (FirebaseAuthException | IllegalArgumentException e ) {
            //log error
            return false;
        }
        return true;
    }
}
