package com.sena;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailSender {

    public void sendEmail(String orderNumber) {
        log.info("Successfully - Order number is {}", orderNumber);
        log.info("Sen e-mail {}", orderNumber);

    }
}