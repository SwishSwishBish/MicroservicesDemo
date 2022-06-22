package com.sena.controller;

import com.sena.repository.IInventoryRepository;
import com.sena.repository.entity.Inventory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final IInventoryRepository inventoryRepository;

    @GetMapping("/{code}")
    Boolean isInStock(@PathVariable String code) {
        log.info("Checking stock for product with code - " + code);
        Inventory inventory = inventoryRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Cannot find product by code " + code));
        return inventory.getStock() > 0;
    }

}