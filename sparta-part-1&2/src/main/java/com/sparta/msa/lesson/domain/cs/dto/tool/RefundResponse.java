package com.sparta.msa.lesson.domain.cs.dto.tool;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefundResponse {

    Boolean success;
    String message;

}