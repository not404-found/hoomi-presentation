package com.hoomicorp.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerifyUserFieldsResponse {
    //username - 1
    //email - 2;
    //phone num - 3
    private String id;
    private boolean error;
    private String message;
}
