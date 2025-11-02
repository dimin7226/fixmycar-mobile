package com.fixmycar.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceRequestDto {
    String description;
    LocalDateTime createdAt;
    String status;
}
