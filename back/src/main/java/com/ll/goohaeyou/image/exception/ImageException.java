package com.ll.goohaeyou.image.exception;

import com.ll.goohaeyou.global.exception.ErrorCode;
import com.ll.goohaeyou.global.exception.GoohaeyouException;

import static com.ll.goohaeyou.global.exception.ErrorCode.*;

public class ImageException extends GoohaeyouException {

    public ImageException(ErrorCode e) {
        super(e);
    }

    public static class FileIsEmptyException extends ImageException {

        public FileIsEmptyException() {
            super(FILE_IS_EMPTY);
        }
    }

    public static class NoFileExtensionException extends ImageException {

        public NoFileExtensionException() {
            super(NO_FILE_EXTENSION);
        }
    }

    public static class InvalidFileExtensionException extends ImageException {

        public InvalidFileExtensionException() {
            super(INVALID_FILE_EXTENSION);
        }
    }

    public static class AWSClientException extends ImageException {

        public AWSClientException() {
            super(AWS_CLIENT_EXCEPTION);
        }
    }

    public static class AWSServiceException extends ImageException {

        public AWSServiceException() {
            super(AWS_SERVICE_EXCEPTION);
        }
    }

    public static class IOExceptionOnImageDeleteException extends ImageException {

        public IOExceptionOnImageDeleteException() {
            super(IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    public static class ProfileImageNotFoundException extends ImageException {

        public ProfileImageNotFoundException() {
            super(PROFILE_IMAGE_NOT_FOUND);
        }
    }

    public static class PostImagesNotFoundException extends ImageException {

        public PostImagesNotFoundException() {
            super(POST_IMAGES_NOT_FOUND);
        }
    }

    public static class ImageNotFoundException extends ImageException {

        public ImageNotFoundException() {
            super(IMAGE_NOT_FOUND);
        }
    }
}
