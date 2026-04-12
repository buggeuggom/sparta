package com.sparta.msa.example_part_3.global.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class HashCodeUtil {

  public String get4CharHash() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 4).toUpperCase();
  }

  public String get8CharHash() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
  }

  public String get12CharHash() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
  }

  public String get24CharHash() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 24).toUpperCase();
  }

}
