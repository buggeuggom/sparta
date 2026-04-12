package com.sparta.msa.lesson.domain.cs.dto.request;

import com.sparta.msa.lesson.global.constants.enums.CustomerPriority;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrioritizedRequest {

    String customerId;
    String message;
    CustomerPriority priority;

}