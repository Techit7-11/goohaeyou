package com.ll.gooHaeYu.standard.base.util;

import com.ll.gooHaeYu.global.config.AppConfig;
import lombok.SneakyThrows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ut {
    public static class str {
        public static boolean isBlank(String str) {
            return str == null || str.trim().length() == 0;
        }

        public static boolean hasLength(String str) {
            return !isBlank(str);
        }
    }

    public static class thread {
        @SneakyThrows
        public static void sleep(long millis) {
            Thread.sleep(millis);
        }
    }

    public static class addr {
        private static final Pattern LOCATION_PATTERN = Pattern.compile("^(서울|부산|대구|인천|광주|대전|울산|세종)|^(.*?[시도])\\s(.*?[시군구])");

        public static String simplifyLocation(String location) {
            Matcher matcher = LOCATION_PATTERN.matcher(location);
            if (matcher.find()) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    if (matcher.group(i) != null) {
                        return matcher.group(i);
                    }
                }
            }
            return location; // 매치되는 패턴이 없다면 원본 문자열 반환
        }
    }

    public static class cmd {

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

    public static class url {
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

    public static class json {
        @SneakyThrows
        public static String toString(Object obj) {
            return AppConfig.getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        }
    }
}
