package com.hoomicorp.configuration;

import com.hoomicorp.security.filter.AuthenticationFilter;
import com.hoomicorp.security.provider.BasicAuthenticationProvider;
import com.hoomicorp.security.provider.FirebaseAuthenticationProvider;
import com.hoomicorp.security.provider.JWTAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    public static final List<String> SECURITY_IGNORE_URLS = List.of("/v2/api-docs",
            "/swagger-resources/configuration/ui",
            "/swagger-resources",
            "/swagger-resources/configuration/security",
            "/swagger-ui.html", "/webjars/**", "/prototype/**", "/management/**", "/api/v1/auth/registration");


    private final AuthenticationFilter tokenFilter;
    private final BasicAuthenticationProvider basicAuthenticationProvider;
    private final FirebaseAuthenticationProvider firebaseAuthenticationProvider;

    public SecurityConfiguration(AuthenticationFilter tokenFilter, BasicAuthenticationProvider basicAuthenticationProvider, FirebaseAuthenticationProvider firebaseAuthenticationProvider) {
        this.tokenFilter = tokenFilter;
        this.basicAuthenticationProvider = basicAuthenticationProvider;
        this.firebaseAuthenticationProvider = firebaseAuthenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(basicAuthenticationProvider);
        auth.authenticationProvider(firebaseAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(SECURITY_IGNORE_URLS.toArray(new String[]{})).permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(tokenFilter, BasicAuthenticationFilter.class)
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors();

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final List<String> allowedOrigins = Collections.singletonList("*");
        final CorsConfiguration configuration = new CorsConfiguration();
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedOrigins);
        configuration.setAllowedHeaders(allowedOrigins);
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
