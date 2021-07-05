package com.hoomicorp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private String name;
    private String displayName;
    private List<String> tags;
    private String imageLink;
//    private String onlineViewersCount;
//    private List<String> onlineViewersId;
}
