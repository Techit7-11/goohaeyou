package com.ll.goohaeyou.payment.payment.application;

import com.ll.goohaeyou.auth.exception.AuthException;
import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.jobApplication.domain.JobApplication;
import com.ll.goohaeyou.jobApplication.domain.repository.JobApplicationRepository;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.CancelPaymentResponse;
import com.ll.goohaeyou.payment.payment.domain.Payment;
import com.ll.goohaeyou.payment.payment.domain.PaymentProcessor;
import com.ll.goohaeyou.payment.payment.domain.repository.PaymentRepository;
import com.ll.goohaeyou.payment.payment.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ll.goohaeyou.global.exception.ErrorCode.BAD_REQUEST;
import static com.ll.goohaeyou.jobApplication.domain.type.WageStatus.PAYMENT_CANCELLED;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {
    private final PaymentRepository paymentRepository;
    private final PaymentProcessor paymentProcessor;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    public CancelPaymentResponse tossPaymentCancel(String username, String paymentKey, String cancelReason) {
        Payment payment = paymentRepository.findByPaymentKeyAndMemberUsername(paymentKey, username)
                .orElseThrow(EntityNotFoundException.PaymentNotFoundException::new);

        checkPaymentCancelable(username, payment);

        payment.cancelPayment(cancelReason);

        JSONObject params  = new JSONObject();
        params.put("cancelReason", cancelReason);

        UpdatePaymentAndApplication(payment, cancelReason);

        // TossPayments 결제 취소 API를 호출하고, 응답 값을 DTO 객체로 매핑
        CancelPaymentResponse response = paymentProcessor.sendPaymentCancelRequest(
                paymentKey, params, CancelPaymentResponse.class
        );

        return response;
    }

    private void checkPaymentCancelable(String username, Payment payment) {
        if (payment.isCanceled()) {
            throw new PaymentException.AlreadyCanceledException();
        }

        if (!payment.getMember().getUsername().equals(username)) {
            throw new AuthException.NotAuthorizedException();
        }

        if (!payment.isPaid()) {
            throw new GoohaeyouException(BAD_REQUEST);
        }
    }

    private void UpdatePaymentAndApplication(Payment payment, String cancelReason) {
        payment.cancelPayment(cancelReason);
        payment.markAsUnpaid();

        JobApplication jobApplication = jobApplicationRepository.findById(payment.getJobApplicationId())
                .orElseThrow(EntityNotFoundException.JobApplicationNotExistsException::new);
        jobApplication.updateWageStatus(PAYMENT_CANCELLED);
        jobApplication.updateEarn(0);
        jobApplication.changeToNotCompleted();
    }
}
