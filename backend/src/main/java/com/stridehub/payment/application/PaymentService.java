package com.stridehub.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stridehub.checkout.domain.CheckoutSession;
import com.stridehub.checkout.domain.CheckoutSessionStatus;
import com.stridehub.checkout.infrastructure.CheckoutSessionRepository;
import com.stridehub.common.exception.AuthenticationFailedException;
import com.stridehub.common.exception.ConflictException;
import com.stridehub.common.exception.NotFoundException;
import com.stridehub.common.time.TimeProvider;
import com.stridehub.config.StridehubProperties;
import com.stridehub.order.application.OrderService;
import com.stridehub.payment.domain.Payment;
import com.stridehub.payment.domain.PaymentAttempt;
import com.stridehub.payment.domain.PaymentStatus;
import com.stridehub.payment.infrastructure.PaymentAttemptRepository;
import com.stridehub.payment.infrastructure.PaymentRepository;
import com.stridehub.payment.infrastructure.provider.PaymentInitiation;
import com.stridehub.payment.infrastructure.provider.PaymentProvider;
import com.stridehub.payment.web.PaymentIntentResponse;
import com.stridehub.payment.web.PaymentWebhookEventRequest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final CheckoutSessionRepository checkoutSessionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;
    private final PaymentProvider paymentProvider;
    private final StridehubProperties stridehubProperties;
    private final TimeProvider timeProvider;
    private final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public PaymentService(
            CheckoutSessionRepository checkoutSessionRepository,
            PaymentRepository paymentRepository,
            PaymentAttemptRepository paymentAttemptRepository,
            PaymentProvider paymentProvider,
            StridehubProperties stridehubProperties,
            TimeProvider timeProvider,
            OrderService orderService
    ) {
        this.checkoutSessionRepository = checkoutSessionRepository;
        this.paymentRepository = paymentRepository;
        this.paymentAttemptRepository = paymentAttemptRepository;
        this.paymentProvider = paymentProvider;
        this.stridehubProperties = stridehubProperties;
        this.timeProvider = timeProvider;
        this.orderService = orderService;
    }

    @Transactional
    public PaymentIntentResponse createPaymentIntent(UUID userId, UUID checkoutSessionId) {
        CheckoutSession checkoutSession = checkoutSessionRepository.findDetailedById(checkoutSessionId)
                .orElseThrow(() -> new NotFoundException("payment.checkout_session_not_found", "Checkout session was not found"));

        if (!checkoutSession.getUser().getId().equals(userId)) {
            throw new NotFoundException("payment.checkout_session_not_found", "Checkout session was not found");
        }

        if (checkoutSession.getStatus() != CheckoutSessionStatus.PENDING_PAYMENT
                && checkoutSession.getStatus() != CheckoutSessionStatus.PAYMENT_INITIATED) {
            throw new ConflictException("payment.invalid_checkout_state", "Checkout session is not payable");
        }

        Payment existingPayment = paymentRepository.findByCheckoutSession_Id(checkoutSessionId).orElse(null);
        if (existingPayment != null) {
            return new PaymentIntentResponse(
                    existingPayment.getId(),
                    existingPayment.getProviderPaymentRef(),
                    "https://mock-gateway.stridehub.local/pay/" + existingPayment.getProviderPaymentRef()
            );
        }

        PaymentInitiation initiation = paymentProvider.initiate(
                checkoutSession.getIdempotencyKey(),
                checkoutSession.getTotalAmount(),
                checkoutSession.getCurrencyCode()
        );

        Payment payment = new Payment(
                UUID.randomUUID(),
                checkoutSession,
                stridehubProperties.getPayment().getProvider(),
                initiation.providerReference(),
                PaymentStatus.INITIATED,
                checkoutSession.getCurrencyCode(),
                checkoutSession.getTotalAmount(),
                "payment-" + UUID.randomUUID()
        );
        payment.markRequiresAction();
        Payment savedPayment = paymentRepository.save(payment);
        paymentAttemptRepository.save(new PaymentAttempt(
                UUID.randomUUID(),
                savedPayment,
                "payment_intent.created",
                savedPayment.getProviderPaymentRef(),
                savedPayment.getProviderPaymentRef(),
                timeProvider.now(),
                true
        ));
        checkoutSession.markPaymentInitiated();

        return new PaymentIntentResponse(
                savedPayment.getId(),
                savedPayment.getProviderPaymentRef(),
                initiation.redirectUrl()
        );
    }

    @Transactional
    public void processWebhook(String rawPayload, String signature) {
        if (!paymentProvider.verifyWebhookSignature(rawPayload, signature)) {
            throw new AuthenticationFailedException("payment.invalid_signature", "Payment webhook signature is invalid");
        }

        PaymentWebhookEventRequest event = parseEvent(rawPayload);
        Payment payment = paymentRepository.findByProviderPaymentRef(event.providerReference())
                .orElseThrow(() -> new NotFoundException("payment.not_found", "Payment was not found"));

        if (paymentAttemptRepository.existsByPayment_IdAndProviderRequestId(payment.getId(), event.eventId())) {
            return;
        }

        Instant attemptedAt = timeProvider.now();
        switch (event.eventType()) {
            case "payment.captured" -> {
                payment.markCaptured(attemptedAt);
                orderService.confirmCapturedPayment(payment);
            }
            case "payment.failed" -> {
                payment.markFailed("provider_reported_failure");
                payment.getCheckoutSession().markPaymentFailed();
            }
            default -> throw new ConflictException("payment.unsupported_event", "Unsupported payment webhook event");
        }

        paymentAttemptRepository.save(new PaymentAttempt(
                UUID.randomUUID(),
                payment,
                event.eventType(),
                event.eventId(),
                event.providerReference(),
                attemptedAt,
                "payment.captured".equals(event.eventType())
        ));
    }

    private PaymentWebhookEventRequest parseEvent(String rawPayload) {
        try {
            return objectMapper.readValue(rawPayload, PaymentWebhookEventRequest.class);
        } catch (Exception exception) {
            throw new ConflictException("payment.invalid_webhook_payload", "Payment webhook payload is invalid");
        }
    }
}
