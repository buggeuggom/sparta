package com.sparta.msa.lesson.domain.cs.dto.response;


import com.sparta.msa.lesson.global.constants.enums.CustomerPriority;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerSupportResponse {

    String customerId;
    String request;
    String response;
    CustomerPriority priority;
    String followUpAction;

}
