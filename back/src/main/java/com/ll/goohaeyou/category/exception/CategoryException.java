package com.ll.goohaeyou.category.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.INVALID_CATEGORY_FORMAT;
import static com.ll.goohaeyou.global.exception.ErrorCode.NOT_FOUND_CATEGORY;

public class CategoryException extends GoohaeyouException {

    public CategoryException(ErrorCode e) {
        super(e);
    }

    public static class InvalidCategoryFormatException extends CategoryException {

        public InvalidCategoryFormatException() {
            super(INVALID_CATEGORY_FORMAT);
        }
    }

    public static class NotFoundCategoryException extends CategoryException {

        public NotFoundCategoryException() {
            super(NOT_FOUND_CATEGORY);
        }
    }
}
