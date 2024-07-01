package com.ll.gooHaeYu.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.gooHaeYu.standard.base.Empty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static lombok.AccessLevel.PRIVATE;

@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@Getter
public class RsData<T> {
    private static final int SUCCESS_CODE_MIN = 200;
    private static final int SUCCESS_CODE_MAX = 399;

    @NonNull
    String resultCode;
    @NonNull
    int statusCode;
    @NonNull
    String msg;
    @NonNull
    T data;

    public static <T> RsData<T> of(T data) {
        return of("200", "OK", data);
    }

    public static <T> RsData<T> of(String resultCode, String msg) {
        return of(resultCode, msg, (T) new Empty());
    }

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        int statusCode = Integer.parseInt(resultCode.split("-", 2)[0]);

        RsData<T> tRsData = new RsData<>(resultCode, statusCode, msg, data);

        return tRsData;
    }

    @JsonIgnore   // 타입스크립트에서 사용X
    public boolean isSuccess() {
        return getStatusCode() >= SUCCESS_CODE_MIN && getStatusCode() < SUCCESS_CODE_MAX;
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
