package com.sena.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface IInventoryClient {
    @GetMapping("/api/inventory/{code}")
    Boolean checkStock(@PathVariable String code);
}
