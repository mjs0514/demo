package com.tmax.core.common;

import org.springframework.stereotype.Service;

@Service
public class CommonService {
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
