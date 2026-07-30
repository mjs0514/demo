package com.tmax.webmvc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

import static com.tmax.webmvc.config.HostFileInterceptor.PathConstants_DIVIDER;

/**
 * master 에서 config-for-webmvc 안쓰이고 있는거 아닌지 확인이 필요
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    public static final String API_PREFIX = "/api";
    public static final String WEBADMIN_PREFIX = "/webadmin";


    final HostFileInterceptor hostFileInterceptor;

    @Value("${MASTER_HOME}")
    private String masterHome;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hostFileInterceptor).addPathPatterns(API_PREFIX + "/hostmanager/hosts/*/files/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(WEBADMIN_PREFIX + "/**")
                .addResourceLocations("file:" + masterHome + "/patch/front/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        List<String> paths = List.of(WEBADMIN_PREFIX, WEBADMIN_PREFIX + PathConstants_DIVIDER);
        paths.forEach(
                path -> registry.addViewController(path).setViewName("redirect:" + WEBADMIN_PREFIX + "/index.html"));
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, clazz -> clazz.isAnnotationPresent(RestController.class));
    }
}
