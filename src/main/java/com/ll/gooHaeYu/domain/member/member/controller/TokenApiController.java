package com.ll.gooHaeYu.domain.member.member.controller;

import com.ll.gooHaeYu.domain.member.member.dto.CreateAccessTokenRequest;
import com.ll.gooHaeYu.domain.member.member.dto.CreateAccessTokenResponse;
import com.ll.gooHaeYu.domain.member.member.service.TokenService;
import com.ll.gooHaeYu.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenApiController {

    private final TokenService tokenService;

    @PostMapping("/api/token")
    public RsData<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return RsData.of("201", "CREATED", new CreateAccessTokenResponse(newAccessToken));
    }
}
