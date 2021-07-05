package com.hoomicorp.controller;

import com.hoomicorp.model.request.RegistrationRequest;
import com.hoomicorp.model.response.AuthToken;
import com.hoomicorp.security.UsernameAndPasswordAuthentication;
import com.hoomicorp.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;


@RestController
@RequestMapping("/api/v1/auth")
@Api(value = "/api/v1/auth", description = "Authentication controller")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/registration")
    @ApiResponses(value = {
            @ApiResponse(code = SC_OK, message = "OK"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Server error")
    })
    private AuthToken registration(@RequestBody RegistrationRequest request) {
        logger.info("[Controller] Registering user");
        final AuthToken response = authService.register(request);
        logger.info("[Controller] User registered");
        return response;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(code = SC_OK, message = "OK"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Server error")
    })
    private AuthToken login() {
        logger.info("[Controller] User logging in");
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final AuthToken authToken = authService.provideToken((UsernameAndPasswordAuthentication) auth);
        logger.info("[Controller] User logged in");
        return authToken;

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/verify")
    @ApiResponses(value = {
            @ApiResponse(code = SC_OK, message = "OK"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Server error")
    })
    private HttpStatus verify() {
        logger.info("[Controller] Verified");
        return HttpStatus.OK;

    }
}
