package com.ll.gooHaeYu.domain.chat.room.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateForm {

    private Long memberId;

}
