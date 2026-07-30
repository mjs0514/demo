package com.tmax.autoconfigure.discovery;

import com.tmax.discovery.MasterPeerRegistryKubernetes;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(KubernetesClient.class)
public class DiscoveryK8sAutoConfiguration {
    @Bean
    MasterPeerRegistryKubernetes masterPeerRegistryKubernetes() {
        return new MasterPeerRegistryKubernetes(new KubernetesClientBuilder().build());
    }
}
