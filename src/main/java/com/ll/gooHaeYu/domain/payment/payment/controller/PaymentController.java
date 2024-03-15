package com.ll.gooHaeYu.domain.payment.payment.controller;

import com.ll.gooHaeYu.domain.payment.payment.dto.PaymentDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.cancel.PaymentCancelResDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.fail.PaymentFailDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.request.PaymentReqDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.request.PaymentResDto;
import com.ll.gooHaeYu.domain.payment.payment.dto.success.PaymentSuccessDto;
import com.ll.gooHaeYu.domain.payment.payment.service.PaymentCancelService;
import com.ll.gooHaeYu.domain.payment.payment.service.PaymentInfoService;
import com.ll.gooHaeYu.domain.payment.payment.service.PaymentService;
import com.ll.gooHaeYu.global.rsData.RsData;
import com.ll.gooHaeYu.global.security.MemberDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentCancelService paymentCancelService;
    private final PaymentInfoService paymentInfoService;

    @PostMapping()
    @Operation(summary = "결제 요청")
    public RsData<PaymentResDto> requestTossPayments(@AuthenticationPrincipal MemberDetails memberDetails,
                                                     @Valid @RequestBody PaymentReqDto paymentReqDto) {

        PaymentResDto paymentResDto = paymentService.requestTossPayment(paymentReqDto, memberDetails.getUsername());

        return RsData.of(paymentResDto);
    }

    @GetMapping("/success")
    @Operation(summary = "결제 성공")
    public RsData<PaymentSuccessDto> tossPaymentSuccess(@RequestParam String paymentKey,
                                        @RequestParam String orderId,
                                        @RequestParam Long amount) {

        return RsData.of(paymentService.tossPaymentSuccess(paymentKey, orderId, amount));
    }

    @GetMapping("/fail")
    @Operation(summary = "결제 실패")
    public RsData<PaymentFailDto> tossPaymentFail(@RequestParam String code,
                                                  @RequestParam String message,
                                                  @RequestParam String orderId) {

        return RsData.of(paymentService.tossPaymentFail(code, message, orderId));
    }

    @PostMapping("/cancel")
    @Operation(summary = "결제 취소")
    public RsData<PaymentCancelResDto> tossPaymentCancel(@AuthenticationPrincipal MemberDetails memberDetails,
                                         @RequestParam String paymentKey,
                                         @RequestParam String cancelReason) {

        PaymentCancelResDto resDto = paymentCancelService.tossPaymentCancel(memberDetails.getUsername(), paymentKey, cancelReason);

        return RsData.of(resDto);
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "결제정보 가져오기")
    public RsData<PaymentDto> getPaymentKey(@AuthenticationPrincipal MemberDetails memberDetails,
                                            @PathVariable Long applicationId) {

        return RsData.of(paymentInfoService.getValidPayment(memberDetails.getUsername(), applicationId));
    }
}
