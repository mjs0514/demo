package com.tmax.apps.controller;

import com.tmax.apps.service.DiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KubernetesController {
    private final DiscoveryService discoveryService;

    public KubernetesController(DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping(path = "/k8s/current-namespace")
    public ResponseEntity<String> getCurrentNamespace() {
        String result = discoveryService.getCurrentNamespace();
        return ResponseEntity.ok(result);
    }
}
