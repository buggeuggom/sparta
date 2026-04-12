package com.sparta.msa.lesson.domain.cs.dto.tool;


import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderQueryResponse {

    String orderId;
    String status;
    List<String> items;
    Double totalPrice;

}
