package com.bit.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NacosTestController {

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test/nacos")
    public Object test() {
        return discoveryClient.getInstances("ms-user");
    }
}
