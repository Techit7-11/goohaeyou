package com.ll.goohaeyou.global.standard.base;

import com.ll.goohaeyou.global.exception.EntityNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static String getNameByCode(int code) {
        for (RegionType region : RegionType.values()) {
            if (region.getCode() == code) {
                return region.getName();
            }
        }
        throw new EntityNotFoundException.NotFoundCategoryException();
    }
}
