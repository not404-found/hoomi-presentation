package com.hoomicorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.List;

@Builder
@ToString
@Getter
@AllArgsConstructor
public class ErrorResponse {

    private String uuid;
    private HttpStatus httpStatus;
    private List<String> messages;


}
