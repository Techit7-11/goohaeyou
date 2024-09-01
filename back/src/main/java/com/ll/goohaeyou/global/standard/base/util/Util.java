package com.ll.goohaeyou.global.standard.base.util;

import com.ll.goohaeyou.global.config.AppConfig;
import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;
import com.ll.goohaeyou.global.standard.base.RegionType;
import lombok.SneakyThrows;

public class Util {

    public static class Region {

        public static int getRegionCodeFromAddress(String roadAddress) {
             if (roadAddress == null || roadAddress.isEmpty()) {
                 throw new GoohaeyouException(ErrorCode.INVALID_ADDRESS_FORMAT);
             }

             for (RegionType region : RegionType.values()) {
                 if (roadAddress.startsWith(region.getName())) {
                     return region.getCode();
                 }
             }

            throw new GoohaeyouException(ErrorCode.INVALID_ADDRESS_FORMAT);
        }
    }

    public static class Cmd {

        public static void runAsync(String cmd) {
            new Thread(() -> {
                run(cmd);
            }).start();
        }

        public static void run(String cmd) {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", cmd);
                // 운영 체제 확인
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    // Windows
                    processBuilder = new ProcessBuilder("cmd", "/c", cmd);
                }

                Process process = processBuilder.start();
                process.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Url {
        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (!url.contains("?")) {
                url += "?";
            }

            if (!url.endsWith("?") && !url.endsWith("&")) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        public static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }
    }

    public static class Json {
        @SneakyThrows
        public static String toString(Object obj) {
            return AppConfig.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }
    }
}
