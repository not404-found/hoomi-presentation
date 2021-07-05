package com.hoomicorp.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerifyUserFieldsRequest {
    //username - 1
    //email - 2;
    //phone num - 3
    private String id;
    private String fieldValue;
}
