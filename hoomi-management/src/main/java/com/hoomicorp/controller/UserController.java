package com.hoomicorp.controller;

import com.hoomicorp.model.request.Request;
import com.hoomicorp.model.response.Response;
import com.hoomicorp.service.UserService;
import com.hoomicorp.util.Log;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController
@RequestMapping("/api/v1/user")
@Api(value = "/api/v1/user", description = "User controller")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/verify/fields")
    @ApiResponses(value = {
            @ApiResponse(code = SC_OK, message = "OK"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "Server error")
    })
    private Response verify(@RequestBody final Request request) {
        if (logger.isDebugEnabled()) {
            logger.debug("[UserController] Verifying user fields {}", request);
        } else {
            logger.info("[UserController] Verifying user fields");
        }
        return userService.verifyUserFields(request);
    }
}
