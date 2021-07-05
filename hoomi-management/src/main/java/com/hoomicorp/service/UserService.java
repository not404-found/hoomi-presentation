package com.hoomicorp.service;

import com.hoomicorp.model.dto.UserInfoDto;
import com.hoomicorp.model.entity.User;
import com.hoomicorp.model.request.RegistrationRequest;
import com.hoomicorp.model.request.Request;
import com.hoomicorp.model.response.Response;

public interface UserService {
    UserInfoDto saveUser(final RegistrationRequest registrationRequest);

    User findUser(final String login, final String password);

    Response verifyUserFields(final Request request);
}
