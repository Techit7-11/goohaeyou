package com.ll.goohaeyou.payment.payment.domain.service;

import com.ll.goohaeyou.global.config.TossPaymentsConfig;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.standard.anotations.DomainService;
import com.ll.goohaeyou.member.member.domain.entity.Member;
import com.ll.goohaeyou.member.member.domain.repository.MemberRepository;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentInitiationResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.PaymentCancelResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import com.ll.goohaeyou.payment.payment.domain.PaymentProcessor;
import com.ll.goohaeyou.payment.payment.domain.entity.Payment;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.domain.util.OrderIdUtil;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import com.ll.goohaeyou.payment.payment.infrastructure.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

@DomainService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentDomainService {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;
    private final TossPaymentsConfig tossPaymentsConfig;
    private final MemberRepository memberRepository;

    @Transactional
    public Payment create(PaymentSuccessResponse successResponse) {
        Member payer = memberRepository.getReferenceById(OrderIdUtil.parseUserIdFromOrderId(successResponse.orderId()));

        if (paymentRepository.existsByOrderName(successResponse.orderName())){
            throw new PaymentException.PaymentRequestConflictException();
        }

        Payment payment = Payment.create(
                successResponse.paymentKey(),
                successResponse.totalAmount(),
                successResponse.method(),
                successResponse.orderId(),
                successResponse.orderName(),
                successResponse.jobApplicationId(),
                payer
        );

        return paymentRepository.save(payment);
    }

    public PaymentInitiationResponse preparePaymentResponse(PaymentRequest request, String username, Long userId) {
        String orderId = OrderIdUtil.generateJobApplicationPaymentId(userId, request.jobApplicationId());

        return PaymentInitiationResponse.of(
                request, orderId, tossPaymentsConfig.getSuccessUrl(), tossPaymentsConfig.getFailUrl(), username
        );
    }

    @Transactional
    public PaymentSuccessResponse requestPaymentAccept(String paymentKey, String orderId, Long amount) {
        JSONObject params = createPaymentRequestParams(orderId, amount);
        PaymentProcessorResponse response = paymentProcessor.sendPaymentRequest(
                paymentKey, params, PaymentProcessorResponse.class
        );

        return PaymentSuccessResponse.from(response, OrderIdUtil.parseJobApplicationIdFromOrderId(orderId));
    }

    private JSONObject createPaymentRequestParams(String orderId, Long amount) {
        JSONObject params = new JSONObject();
        params.put("orderId", orderId);
        params.put("amount", amount);
        return params;
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

    public Payment getPaidByJobApplicationId(Long jobApplicationId) {
        return paymentRepository.findByJobApplicationIdAndPaid(jobApplicationId, true)
                .orElseThrow(PaymentException.PaymentInfoNotFoundException::new);
    }

    public Payment getByPaymentKeyAndUsername(String paymentKey, String username) {
        return paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);
    }

    public Payment getByPaymentKey(String paymentKey) {
        return paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);
    }
}
