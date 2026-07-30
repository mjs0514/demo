package com.tmax.apps.controller;

import com.tmax.core.common.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {
    private final CommonService commonService;

    public CommonController(CommonService commonService) {
        this.commonService = commonService;
    }

    @PostMapping(path = "/hello&name={name}")
    public ResponseEntity<String> sayHello(@PathVariable String name) {
        String result = commonService.sayHello(name);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/boot-on-cloud")
    public ResponseEntity<String> getBootOnCloud() {
        String result = commonService.getBootOnCloud();
        return ResponseEntity.ok(result);
    }
}
