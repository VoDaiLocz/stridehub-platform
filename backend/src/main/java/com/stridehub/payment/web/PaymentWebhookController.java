package com.stridehub.payment.web;

import com.stridehub.config.StridehubProperties;
import com.stridehub.payment.application.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/webhooks/payment")
public class PaymentWebhookController {

    private final PaymentService paymentService;
    private final StridehubProperties stridehubProperties;

    public PaymentWebhookController(PaymentService paymentService, StridehubProperties stridehubProperties) {
        this.paymentService = paymentService;
        this.stridehubProperties = stridehubProperties;
    }

    @PostMapping("/provider")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleProviderWebhook(
            HttpServletRequest request,
            @RequestBody String rawPayload
    ) {
        String signature = request.getHeader(stridehubProperties.getPayment().getWebhookSignatureHeader());
        paymentService.processWebhook(rawPayload, signature);
    }
}
