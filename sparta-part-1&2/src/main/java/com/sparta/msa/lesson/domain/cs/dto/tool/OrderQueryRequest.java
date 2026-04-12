package com.sparta.msa.lesson.domain.cs.dto.tool;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderQueryRequest {

    @JsonPropertyDescription("정보를 조회할 주문 번호 (예: ORD-12345)")
    String orderId;

}