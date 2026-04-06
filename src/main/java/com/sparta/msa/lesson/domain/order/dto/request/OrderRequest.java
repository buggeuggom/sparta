package com.sparta.msa.lesson.domain.order.dto.request;


import com.sparta.msa.lesson.global.constants.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    @NotNull
    Long userId;

    @NotEmpty
    List<OrderProductRequest> products;

    @Getter
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderProductRequest {

        @NotNull
        Long id;

        @Min(1)
        Integer quantity;

    }
}
