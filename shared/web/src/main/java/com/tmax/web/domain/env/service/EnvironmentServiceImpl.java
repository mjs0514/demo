package com.tmax.web.domain.env.service;

import com.tmax.web.domain.env.dto.EnvironmentDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class EnvironmentServiceImpl implements EnvironmentService {
    @Override
    public EnvironmentDTO save(EnvironmentDTO environmentDTO) {
        log.info("save operation - environmentDTO={}", environmentDTO);
        return environmentDTO;
    }

    @Override
    public EnvironmentDTO findEnvironment() {
        return EnvironmentDTO.builder()
                .allowNameMasking(true)
                .allowConcurrentLogin(true)
                .loginValidSeconds(10)
                .build();
    }
}
