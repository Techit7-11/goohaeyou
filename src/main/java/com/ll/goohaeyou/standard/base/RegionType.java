package com.ll.goohaeyou.standard.base;

import com.ll.goohaeyou.global.exception.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

@Getter
@RequiredArgsConstructor
public enum RegionType {
    SEOUL("서울", 101),
    GYEONGGI("경기", 102),
    BUSAN("부산", 103),
    GYEONGNAM("경남", 104),
    INCHEON("인천", 105),
    GYEONGBUK("경북", 106),
    DAEGU("대구", 107),
    CHUNGNAM("충남", 108),
    JEONNAM("전남", 109),
    JEONBUK("전북", 110),
    CHUNGBUK("충북", 111),
    GANGWON("강원", 112),
    DAEJEON("대전", 113),
    GWANGJU("광주", 114),
    ULSAN("울산", 115),
    JEJU("제주", 116),
    SEJONG("세종", 117);

    private final String name;
    private final int code;

    public static int getCodeByName(String name) {
        for (RegionType region : RegionType.values()) {
            if (region.getName().equals(name)) {
                return region.getCode();
            }
        }
        throw new CustomException(NOT_FOUND_CATEGORY);
    }
}
