package com.hoomicorp.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UsernameAndPasswordAuthentication extends UsernamePasswordAuthenticationToken {
    private String id;

    public UsernameAndPasswordAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public UsernameAndPasswordAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
