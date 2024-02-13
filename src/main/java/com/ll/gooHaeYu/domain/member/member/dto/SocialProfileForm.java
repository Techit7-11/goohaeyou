package com.ll.gooHaeYu.domain.member.member.dto;

import com.ll.gooHaeYu.domain.member.member.entity.type.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
=======
import lombok.Builder;
import lombok.Getter;
>>>>>>> main
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
<<<<<<< HEAD
@NoArgsConstructor
@AllArgsConstructor
=======
>>>>>>> main
public class SocialProfileForm {   // JorinForm에서 username, password 제외

    @NotBlank(message = "이름은 필수 항목입니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "이름은 한글 또는 영문만 가능합니다.")
    private String name;

    @NotBlank(message = "휴대폰 번호는 필수 항목입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰 번호는 숫자만 10자리 또는 11자리여야 합니다.")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "지역을 입력해주세요.")
    private String location;

    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    @Past(message = "생년월일은 현재보다 앞선 날짜여야 합니다.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
}
