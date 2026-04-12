package com.sparta.msa.lesson.domain.cs.dto.tool;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRecommendationRequest {

    @JsonPropertyDescription("추천 대상 고객의 고유 ID")
    String customerId;

    @JsonPropertyDescription("특정하고 싶은 카테고리 (예: 전자기기, 의류)")
    String category;

}