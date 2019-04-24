package com.silaev.packer.exception;

/**
 * public static String pack(String filePath) throws APIException.
 * That's why we extend Exception here, because:
 * You only need to include a throws clause on a method if the method throws
 * a checked exception. If the method throws a runtime exception
 * then there is no need to do so.
 */
public class APIException extends Exception {
    public APIException(String message) {
        super(message);
    }

    public APIException(String message, Throwable cause) {
        super(message, cause);
    }
}
