package com.hoomicorp.service;

import com.hoomicorp.model.dto.UserInfoDto;
import com.hoomicorp.model.entity.User;
import com.hoomicorp.model.request.RegistrationRequest;
import com.hoomicorp.model.response.AuthToken;
import com.hoomicorp.security.UsernameAndPasswordAuthentication;
import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthToken provideToken(final UsernameAndPasswordAuthentication authentication);
    UserInfoDto login(final String username, final String password);
    AuthToken register(final RegistrationRequest registrationRequest);

}
