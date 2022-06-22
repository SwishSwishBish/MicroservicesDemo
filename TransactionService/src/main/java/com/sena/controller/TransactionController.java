package com.sena.controller;

import com.sena.client.IInventoryClient;
import com.sena.dto.TransactionDto;
import com.sena.repository.ITransactionRespository;
import com.sena.repository.entity.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionRespository orderRepository;
    private final IInventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final StreamBridge streamBridge;
    private final ExecutorService traceableExecutorService;

    @PostMapping
    public String placeTransaction(@RequestBody TransactionDto orderDto) {
        circuitBreakerFactory.configureExecutorService(traceableExecutorService);
        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        java.util.function.Supplier<Boolean> booleanSupplier = () -> orderDto.getOrderLineItemsList().stream()
                .allMatch(lineItem -> {
                    log.info("Making call to inventory service for code {}", lineItem.getCode());
                    return inventoryClient.checkStock(lineItem.getCode());
                });
        boolean productsInStock = circuitBreaker.run(booleanSupplier, throwable -> handleErrorCase());

        if (productsInStock) {
            Transaction order = new Transaction();
            order.setTransactionItems(orderDto.getOrderLineItemsList());
            order.setOrderNumber(UUID.randomUUID().toString());

            orderRepository.save(order);
            log.info("Sending order details with order Id {} to notification service", order.getId());
            streamBridge.send("notificationEventSupplier-out-0", MessageBuilder.withPayload(order.getId()).build());
            return "Successfully";
        } else {
            return "Failed";
        }
    }
    private Boolean handleErrorCase() {
        return false;
    }
}
