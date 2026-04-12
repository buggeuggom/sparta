package com.sparta.msa.lesson.domain.cs.dto.tool;


import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefundRequest {

    @JsonPropertyDescription("환불을 진행할 주문 번호")
    String orderId;

    @JsonPropertyDescription("고객이 요청한 구체적인 환불 사유")
    String reason;

}