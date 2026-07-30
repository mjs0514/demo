package com.tmax.web.domain.env.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvironmentDTO {
    private boolean allowConcurrentLogin;

    private Integer loginValidSeconds;

    private boolean allowNameMasking;
}
