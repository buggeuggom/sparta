package com.sparta.msa.lesson.domain.agent.dto.request;


import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LimitedAgentRequest {

    String goal;
    List<String> tools;

}