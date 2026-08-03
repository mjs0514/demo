package com.tmax.core.common;

import com.tmax.common.config.EnvironmentValueProvider;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonServiceTest {
    @Test
    void say_hello_test() {
        CommonService cs = new CommonService(null);
        String actual = cs.sayHello("tmax");

        assertThat(actual).isEqualTo("Hello tmax");
    }

    @Test
    void boot_on_cloud_test() {
        EnvironmentValueProvider provider = new EnvironmentValueProvider(true);
        CommonService cs = new CommonService(provider);
        String actual = cs.getBootOnCloud();

        assertThat(actual).isEqualTo("master-boot-on-cloud: true");
    }
}
