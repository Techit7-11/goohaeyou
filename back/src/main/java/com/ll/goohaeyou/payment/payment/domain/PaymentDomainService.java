package com.ll.goohaeyou.payment.payment.domain;

import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentInfoResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.PaymentCancelResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.domain.type.PayStatus;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import com.ll.goohaeyou.payment.payment.infrastructure.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentDomainService {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;
    private final TossPaymentsConfig tossPaymentsConfig;

    @Transactional
    public Payment create(PaymentRequest request, Member member) {
        Payment payment = Payment.create(
                request.amount(),
                request.payStatus().getDescription(),
                UUID.randomUUID().toString(),
                request.orderName(),
                request.jobApplicationId()
        );

        payment.updatePayer(member);

        return paymentRepository.save(payment);
    }

    public PaymentResponse preparePaymentResponse(Payment payment) {
        return PaymentResponse.from(payment, tossPaymentsConfig.getSuccessUrl(), tossPaymentsConfig.getFailUrl());
    }

    public Payment getByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        JSONObject params = createPaymentRequestParams(orderId, amount);
        PaymentProcessorResponse response = paymentProcessor.sendPaymentRequest(paymentKey, params, PaymentProcessorResponse.class);

        Long jobApplicationId = getByOrderId(orderId).getJobApplicationId();

        return PaymentSuccessResponse.from(
                response.getPaymentKey(),
                response.getOrderId(),
                jobApplicationId,
                response.getOrderName(),
                response.getMethod(),
                response.getTotalAmount(),
                response.getApprovedAt(),
                response.getCard(),
                response.getEasyPay()
        );
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);
        return params;
    }

    @Transactional
    public void update(Payment payment, PaymentSuccessResponse successResponse) {
        payment.updatePaymentKey(successResponse.paymentKey());
        payment.markAsPaid();
        payment.updatePayStatus(PayStatus.findByMethod(successResponse.method()));
    }

    @Transactional
    public void delete(Payment payment) {
        paymentRepository.delete(payment);
    }

    @Transactional
    public void cancel(Payment payment, String cancelReason) {
        payment.cancelPayment(cancelReason);
        payment.markAsUnpaid();
    }

    public PaymentCancelResponse sendPaymentCancelRequest(String paymentKey, String cancelReason) {
        JSONObject params = new JSONObject();
        params.put("cancelReason", cancelReason);

        return paymentProcessor.sendPaymentCancelRequest(paymentKey, params, PaymentCancelResponse.class);
    }

    public Payment getPaymentInfo(Long jobApplicationId) {
        return paymentRepository.findByJobApplicationIdAndPaid(jobApplicationId, true)
                .orElseThrow(PaymentException.PaymentInfoNotFoundException::new);
    }

    public Payment getByPaymentKeyAndUsername(String paymentKey, String username) {
        return paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);
    }
}
