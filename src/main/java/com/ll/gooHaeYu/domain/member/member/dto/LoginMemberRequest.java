package com.ll.gooHaeYu.domain.member.member.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberRequest {

    @NotBlank(message = "아이디는 필수 항목입니다.")
    @Size(min = 4, max = 20, message = "비밀번호는 최소 4자 ~ 최대 20자 이내로 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 최소 8자 ~ 최대 20자 이내로 입력해주세요.")
    private String password;
}
