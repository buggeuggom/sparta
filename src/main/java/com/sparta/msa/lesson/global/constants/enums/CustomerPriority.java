package com.sparta.msa.lesson.global.constants.enums;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum CustomerPriority {
    VIP("VIP 고객"),
    URGENT("긴급"),
    NORMAL("일반");

    String description;
}