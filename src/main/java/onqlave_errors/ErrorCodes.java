package onqlave_errors;

import java.util.Arrays;

public enum ErrorCodes {
    Server("Server"),
    InvalidInput("InvalidInput"),
    SdkErrorCode("400");

    private final String code;

    ErrorCodes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

