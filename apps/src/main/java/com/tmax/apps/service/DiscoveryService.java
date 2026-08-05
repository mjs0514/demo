package com.tmax.apps.service;

import com.tmax.discovery.MasterPeerRegistryKubernetes;
import org.springframework.stereotype.Service;

@Service
public class DiscoveryService {
    private final MasterPeerRegistryKubernetes k8s;

    public DiscoveryService(MasterPeerRegistryKubernetes k8s) {
        this.k8s = k8s;
    }

    public String getCurrentNamespace() {
        return "k8s namespace=" + k8s.getCurrentNamespace();
    }
}
