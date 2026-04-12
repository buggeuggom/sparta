package com.sparta.msa.lesson.domain.cs.controller;


import com.sparta.msa.lesson.domain.cs.dto.request.CustomerRequest;
import com.sparta.msa.lesson.domain.cs.dto.request.MultilingualRequest;
import com.sparta.msa.lesson.domain.cs.dto.request.PrioritizedRequest;
import com.sparta.msa.lesson.domain.cs.dto.response.CustomerSupportResponse;
import com.sparta.msa.lesson.domain.cs.service.CustomerSupportAgent;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cs")
public class CustomerSupportController {

    private final CustomerSupportAgent customerSupportAgent;

    /**
     * 1. 표준 요청 처리
     */
    @PostMapping
    public ApiResponse<String> handleCustomerRequest(@RequestBody CustomerRequest request) {
        log.info("[API] 표준 CS 요청: {}", request.getCustomerId());

        String result = customerSupportAgent.handleCustomerRequest(
                request.getCustomerId(), request.getMessage());

        return ApiResponse.ok(result);
    }

    /**
     * 2. 우선순위 기반 응대
     */
    @PostMapping("/priority")
    public ApiResponse<CustomerSupportResponse> handlePrioritizedRequest(
            @RequestBody PrioritizedRequest request) {
        log.info("[API] 우선순위 CS 요청: {}, 등급: {}", request.getCustomerId(), request.getPriority());

        CustomerSupportResponse result = customerSupportAgent.handlePrioritizedRequest(
                request.getCustomerId(), request.getMessage(), request.getPriority());

        return ApiResponse.ok(result);
    }

    /**
     * 3. 감정 분석 기반 대응
     */
    @PostMapping("/sentiment")
    public ApiResponse<String> handleCustomerRequestWithSentiment(@RequestBody CustomerRequest request) {
        log.info("[API] 감정 분석 CS 요청: {}", request.getCustomerId());

        String result = customerSupportAgent.handleCustomerRequestWithSentiment(
                request.getCustomerId(), request.getMessage());

        return ApiResponse.ok(result);
    }

    /**
     * 4. 다국어 지원
     */
    @PostMapping("/multilingual")
    public ApiResponse<String> handleMultilingualRequest(@RequestBody MultilingualRequest request) {
        log.info("[API] 다국어 CS 요청: {}, 언어: {}", request.getCustomerId(), request.getLanguage());

        String result = customerSupportAgent.handleMultilingualRequest(
                request.getCustomerId(), request.getMessage(), request.getLanguage());

        return ApiResponse.ok(result);
    }

}