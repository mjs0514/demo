package com.tmax.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// apps ->

/**
 * apps -> shared/common
 * core -> shared/common
 *
 * 참조를 빈으로 해야되는 경우임
 * 추후에 pluggable 하게 앱이 분리되면 추후에 어떤 방식으로 master-boot-on-cloud 설정을 공유할 수 있을지 고민이 필요함
 */
@Component
public class EnvironmentValueProvider {
    private final boolean bootOnCloud;

    public EnvironmentValueProvider(@Value("${master-boot-on-cloud:false}") boolean bootOnCloud) {
        this.bootOnCloud = bootOnCloud;
    }

    public boolean isBootOnCloud() {
        return bootOnCloud;
    }
}
