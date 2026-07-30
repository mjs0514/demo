package com.tmax.discovery;

import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * discovery k8s 이라는 도메인 영역
 */
public class MasterPeerRegistryKubernetes {
    private final KubernetesClient kubernetesClient;

    public MasterPeerRegistryKubernetes(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    public String getCurrentNamespace() {
        return kubernetesClient.getNamespace() == null ? "default" : kubernetesClient.getNamespace();
    }
}
