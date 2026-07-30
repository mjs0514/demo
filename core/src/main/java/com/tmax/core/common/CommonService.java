package com.tmax.core.common;

import com.tmax.common.config.EnvironmentValueProvider;
import org.springframework.stereotype.Service;

@Service
public class CommonService {
    private final EnvironmentValueProvider environmentValueProvider;

    public CommonService(EnvironmentValueProvider environmentValueProvider) {
        this.environmentValueProvider = environmentValueProvider;
    }

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String getBootOnCloud() {
        return "master-boot-on-cloud: " + environmentValueProvider.isBootOnCloud();
    }
}
