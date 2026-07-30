package com.tmax.web.domain.env.controller;

import com.tmax.web.domain.env.dto.EnvironmentDTO;
import com.tmax.web.domain.env.service.EnvironmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnvironmentController {
    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @PutMapping("/env")
    public EnvironmentDTO updateEnvironment(@RequestBody EnvironmentDTO environmentDTO) {
        return environmentService.save(environmentDTO);
    }

    @GetMapping("/env")
    public EnvironmentDTO getEnvironment() {
        return environmentService.findEnvironment();
    }
}
