package com.stridehub.checkout.application;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CheckoutExpiryScheduler {

    private final CheckoutService checkoutService;

    public CheckoutExpiryScheduler(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @Scheduled(fixedDelay = 60000)
    public void expireOverdueSessions() {
        checkoutService.expireExpiredSessions();
    }
}
