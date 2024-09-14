package com.ll.goohaeyou.global.standard.base.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MimeTypeUtil {
    private static final Map<String, String> EXTENSION_TO_MIME_MAP = new HashMap<>();

    static {
        EXTENSION_TO_MIME_MAP.put("jpg", "image/jpeg");
        EXTENSION_TO_MIME_MAP.put("jpeg", "image/jpeg");
        EXTENSION_TO_MIME_MAP.put("png", "image/png");
        EXTENSION_TO_MIME_MAP.put("gif", "image/gif");
        EXTENSION_TO_MIME_MAP.put("bmp", "image/bmp");
        EXTENSION_TO_MIME_MAP.put("tiff", "image/tiff");
        EXTENSION_TO_MIME_MAP.put("svg", "image/svg+xml");
    }

    public static Optional<String> getMimeType(String extention) {
        return Optional.of(EXTENSION_TO_MIME_MAP.get(extention).toLowerCase());
    }
}
