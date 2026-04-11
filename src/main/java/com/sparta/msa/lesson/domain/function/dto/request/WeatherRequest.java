package com.sparta.msa.lesson.domain.function.dto.request;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WeatherRequest {

    @JsonPropertyDescription("날씨를 조회할 도시 이름 (예: 서울, 도쿄, 뉴욕)")
    private String city;

}