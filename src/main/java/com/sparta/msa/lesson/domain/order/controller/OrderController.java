package com.sparta.msa.lesson.domain.order.controller;

import com.sparta.msa.lesson.domain.order.dto.request.OrderRequest;
import com.sparta.msa.lesson.domain.order.service.OrderService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<Void> placeOrder(@Valid @RequestBody OrderRequest request) {
        orderService.order(request);
        return ApiResponse.ok();
    }
}