package com.tmax.web.domain.env.service;

import com.tmax.web.domain.env.dto.EnvironmentDTO;

public interface EnvironmentService {
    EnvironmentDTO save(EnvironmentDTO environmentDTO);

    EnvironmentDTO findEnvironment();
}
