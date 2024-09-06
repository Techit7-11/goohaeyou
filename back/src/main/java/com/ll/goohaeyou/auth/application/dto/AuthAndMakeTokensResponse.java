package com.ll.goohaeyou.auth.application.dto;

import com.ll.goohaeyou.member.member.application.dto.MemberResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class AuthAndMakeTokensResponse {
    @NonNull
    MemberResponse memberResponse;
    @NotNull
    String accessToken;
    @NotNull
    String refreshToken;
}
