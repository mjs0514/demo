package com.tmax.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class EnvironmentValueProviderTest {
    @Test
    void test() {
        EnvironmentValueProvider provider = new EnvironmentValueProvider(true);

        assertThat(provider.isBootOnCloud()).isEqualTo(true);
    }
}
