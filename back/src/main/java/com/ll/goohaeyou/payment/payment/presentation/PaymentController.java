package com.ll.goohaeyou.payment.payment.presentation;

import com.ll.goohaeyou.payment.cashLog.application.CashLogService;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentDto;
import com.ll.goohaeyou.payment.payment.application.dto.cancel.CancelPaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.fail.PaymentFailResponse;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentRequest;
import com.ll.goohaeyou.payment.payment.application.dto.PaymentResponse;
import com.ll.goohaeyou.payment.payment.application.dto.success.PaymentSuccessDto;
import com.ll.goohaeyou.payment.payment.application.PaymentCancelService;
import com.ll.goohaeyou.payment.payment.application.PaymentInfoService;
import com.ll.goohaeyou.payment.payment.application.PaymentService;
import com.ll.goohaeyou.global.apiResponse.ApiResponse;
import com.ll.goohaeyou.auth.domain.MemberDetails;
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
    public ApiResponse<PaymentResponse> requestTossPayments(@AuthenticationPrincipal MemberDetails memberDetails,
                                                            @Valid @RequestBody PaymentRequest request) {

        PaymentResponse paymentResponse = paymentService.requestTossPayment(request, memberDetails.getUsername());

        return ApiResponse.ok(paymentResponse);
    }

    @GetMapping("/success")
    @Operation(summary = "결제 성공")
    public ApiResponse<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey,
                                                                  @RequestParam String orderId,
                                                                  @RequestParam Long amount) {
        PaymentSuccessDto successDto = paymentService.tossPaymentSuccess(paymentKey, orderId, amount);
        cashLogService.createCashLogOnPaid(successDto);

        return ApiResponse.ok(successDto);
    }

    @GetMapping("/fail")
    @Operation(summary = "결제 실패")
    public ApiResponse<PaymentFailResponse> tossPaymentFail(@RequestParam String code,
                                                            @RequestParam String message,
                                                            @RequestParam String orderId) {

        return ApiResponse.ok(paymentService.tossPaymentFail(code, message, orderId));
    }

    @PostMapping("/cancel")
    @Operation(summary = "결제 취소")
    public ApiResponse<CancelPaymentResponse> tossPaymentCancel(@AuthenticationPrincipal MemberDetails memberDetails,
                                                              @RequestParam String paymentKey,
                                                              @RequestParam String cancelReason) {

        CancelPaymentResponse resDto = paymentCancelService.tossPaymentCancel(memberDetails.getUsername(), paymentKey, cancelReason);
        cashLogService.createCashLogOnCancel(paymentKey);

        return ApiResponse.ok(resDto);
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "결제정보 가져오기")
    public ApiResponse<PaymentDto> getPaymentKey(@AuthenticationPrincipal MemberDetails memberDetails,
                                                 @PathVariable Long applicationId) {

        return ApiResponse.ok(paymentInfoService.getValidPayment(memberDetails.getUsername(), applicationId));
    }
}
