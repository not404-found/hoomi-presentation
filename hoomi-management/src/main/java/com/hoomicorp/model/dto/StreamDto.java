package com.hoomicorp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@JsonIgnoreProperties(value = { "scheduledDate" })
public class StreamDto {
    private String userId;
    private String streamId;
    private String streamName;
    private String streamerId;
    private String streamerName;
    private String streamerProfileImageLink;
    private List<String> categories;
    private String imageLink;
    private String streamLink;
    private String recordingLink;
    private String previewLink;
    private String entertainmentName;
    private String entertainmentId;
    private String scheduledDate;
    private String streamedDate;
    private List<String> visitedViewersId;
    private List<String> waitingViewersId;
    private List<String> onlineViewersId;
    private Double cost;
    private List<String> likedViewersId;
    private List<String> dislikedViewersId;
    private boolean removed;
}
