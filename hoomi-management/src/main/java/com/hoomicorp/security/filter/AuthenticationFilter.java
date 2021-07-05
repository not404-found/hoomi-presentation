package com.hoomicorp.security.filter;

import com.hoomicorp.security.TokenAuthentication;
import com.hoomicorp.security.UsernameAndPasswordAuthentication;
import com.hoomicorp.security.provider.JWTTokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.function.Predicate;

import static com.hoomicorp.configuration.SecurityConfiguration.SECURITY_IGNORE_URLS;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    private static final String BEARER_AUTH_HEADER_PREFIX = "Bearer ";
    private static final String BASIC_AUTH_HEADER_PREFIX = "Basic ";

    private static final String EMPTY = "";
    private static final String COLON = ":";

    private final JWTTokenProvider tokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthenticationFilter(JWTTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String header = request.getHeader(AUTHENTICATION_HEADER_NAME);
        final String authValue = resolveToken(header);

        validate(authValue, "Credentials not found", Objects::isNull);

        if (header.contains(BASIC_AUTH_HEADER_PREFIX)) {
            basicAuthentication(authValue);
        } else {
            tokenAuthentication(authValue);
        }
        filterChain.doFilter(request, response);

    }

    //username:password in base 64
    //username
    //password
    private void basicAuthentication(String authenticationHeader) {
        String encodedAuth = authenticationHeader.replace(BASIC_AUTH_HEADER_PREFIX, EMPTY);
        String decodedAuth = decode(encodedAuth);

        String[] usernameAndPassword = decodedAuth.split(COLON);

        validate(usernameAndPassword, "Credentials not found", val -> val.length < 2);

        String username = usernameAndPassword[0];
        String password = usernameAndPassword[1];

        Authentication authentication = new UsernameAndPasswordAuthentication(username, password, new ArrayList<>());
        //is not auth yet
        authentication.setAuthenticated(false);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void tokenAuthentication(String token) {
        final TokenAuthentication authentication = new TokenAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private String decode(String val) {
        validate(val, "Cant decode from base 64 cause value is null", Objects::isNull);

        byte[] decodedBytes = Base64.getDecoder().decode(val);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public String resolveToken(final String header) {
        if (Objects.nonNull(header)) {
            if (header.startsWith(BEARER_AUTH_HEADER_PREFIX)) {
                // Bearer sometoken
                return header.substring(7);
            } else {
                return header.substring(6);
            }
        }
        return null;
    }


    private <T> void validate(final T forCheck, final String errMessage, Predicate<T> predicate) {

        if (predicate.test(forCheck)) {
            throw new BadCredentialsException(errMessage);
        }
    }

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        final String servletPath = request.getServletPath();

        return SECURITY_IGNORE_URLS.stream()
                .anyMatch(p -> pathMatcher.match(p, servletPath));
    }


}
