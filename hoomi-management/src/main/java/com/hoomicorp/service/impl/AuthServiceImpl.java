package com.hoomicorp.service.impl;

import com.hoomicorp.exception.AuthenticationException;
import com.hoomicorp.exception.UserAlreadyExistsException;
import com.hoomicorp.model.dto.UserInfoDto;
import com.hoomicorp.model.entity.User;
import com.hoomicorp.model.entity.enums.Status;
import com.hoomicorp.model.request.RegistrationRequest;
import com.hoomicorp.model.response.AuthToken;
import com.hoomicorp.repository.UserRepository;
import com.hoomicorp.security.UsernameAndPasswordAuthentication;
import com.hoomicorp.security.provider.FirebaseTokenProvider;
import com.hoomicorp.service.AuthService;
import com.hoomicorp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final FirebaseTokenProvider tokenProvider;

    @Autowired
    public AuthServiceImpl(UserService userService, FirebaseTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public AuthToken register(final RegistrationRequest registrationRequest) {
        final UserInfoDto info = userService.saveUser(registrationRequest);
        final String token = tokenProvider.createToken(info.getId());
        return AuthToken.builder().token(token).userId(info.getId()).build();
    }


    @Override
    public AuthToken provideToken(final UsernameAndPasswordAuthentication authentication) {
        final String id = authentication.getId();
        final String token = tokenProvider.createToken(String.valueOf(id));
        return AuthToken.builder().userId(id).token(token).build();
    }

    @Override
    public UserInfoDto login(String username, String password) {
        final User user = userService.findUser(username, password);

        if (Objects.isNull(user)) {
            throw new BadCredentialsException("Provided username or password incorrect");
        }

        if (Objects.equals(user.getStatus(), Status.DELETED) || Objects.equals(user.getStatus(), Status.BLOCKED)) {
            throw new AuthenticationException("Account blocked or deleted");
        }
        return UserInfoDto.builder()
                .id(user.getId()).name(user.getUsername()).build();
    }
}
