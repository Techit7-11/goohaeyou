package com.ll.goohaeyou.payment.payment.presentation;

import com.ll.goohaeyou.auth.domain.MemberDetails;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.payment.cashLog.application.CashLogService;
import com.ll.goohaeyou.payment.payment.application.PaymentCancelService;
import com.ll.goohaeyou.payment.payment.application.PaymentInfoService;
import com.ll.goohaeyou.payment.payment.application.PaymentService;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentInfoResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentInitiationResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.PaymentCancelResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 관련 API")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentInfoService paymentInfoService;
    private final CashLogService cashLogService;

    @PostMapping()
    @Operation(summary = "결제 요청")
    public ApiResponse<PaymentInitiationResponse> requestTossPayments(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                      @Valid @RequestBody PaymentRequest request) {

        PaymentInitiationResponse paymentInitiationResponse = paymentService.requestTossPayment(request, memberDetails.getUsername());

        return ApiResponse.ok(paymentInitiationResponse);
    }

    @GetMapping("/success")
    @Operation(summary = "결제 성공")
    public ApiResponse<PaymentSuccessResponse> tossPaymentSuccess(@RequestParam String paymentKey,
                                                                  @RequestParam String orderId,
                                                                  @RequestParam Long amount) {
        PaymentSuccessResponse successDto = paymentService.tossPaymentSuccess(paymentKey, orderId, amount);
        cashLogService.createCashLogOnPaid(successDto);

        return ApiResponse.ok(successDto);
    }

    @PostMapping("/cancel")
    @Operation(summary = "결제 취소")
    public ApiResponse<PaymentCancelResponse> tossPaymentCancel(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                @RequestParam String paymentKey,
                                                                @RequestParam String cancelReason) {

        PaymentCancelResponse resDto = paymentCancelService.tossPaymentCancel(memberDetails.getUsername(), paymentKey, cancelReason);
        cashLogService.createCashLogOnCancel(paymentKey);

        return ApiResponse.ok(resDto);
    }

    @GetMapping("/{jobApplicationId}")
    @Operation(summary = "결제 정보 가져오기")
    public ApiResponse<PaymentInfoResponse> getPaymentInfo(@AuthenticationPrincipal MemberDetails memberDetails,
                                                           @PathVariable Long jobApplicationId) {

        return ApiResponse.ok(paymentInfoService.getPaymentInfo(memberDetails.getUsername(), jobApplicationId));
    }
}
