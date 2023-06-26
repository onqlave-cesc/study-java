package onqlave_messages;

public class Messages {
    public static final String SDK = "SDK";

    public static final String FETCHING_ENCRYPTION_KEY_OPERATION = "[onqlave] SDK: %s - Fetching encryption key";
    public static final String FETCHING_ENCRYPTION_KEY_RESPONSE_UNMARSHALING_FAILED = "[onqlave] SDK: %s - Failed unmarshalling encryption key response";
    public static final String FETCHED_ENCRYPTION_KEY_OPERATION = "[onqlave] SDK: %s - Fetched encryption key: operation took %s";

    public static final String FETCHING_DECRYPTION_OPERATION = "[onqlave] SDK: %s - Fetching decryption key";
    public static final String FETCHING_DECRYPTION_KEY_RESPONSE_UNMARSHALING_FAILED = "[onqlave] SDK: %s - Failed unmarshalling decryption key response";
    public static final String FETCHED_DECRYPTION_OPERATION = "[onqlave] SDK: %s - Fetched decryption key: operation took %s";

    public static final String KEY_INVALID_WRAPPING_ALGO = "[onqlave] SDK: %s - Invalid wrapping algorithm";
    public static final String KEY_INVALID_WRAPPING_OPERATION = "[onqlave] SDK: %s - Invalid wrapping operation";
    public static final String KEY_UNWRAPPING_KEY_FAILED = "[onqlave] SDK: %s - Failed unwrapping encryption key";
    public static final String KEY_INVALID_ENCRYPTION_OPERATION = "[onqlave] SDK: %s - Invalid encryption operation";
    public static final String KEY_INVALID_DECRYPTION_OPERATION = "[onqlave] SDK: %s - Invalid encryption operation";

    public static final String ENCRYPTING_OPERATION = "[onqlave] SDK: %s - Encrypting plain data";
    public static final String ENCRYPTED_OPERATION = "[onqlave] SDK: %s - Encrypted plain data: operation took %s";
    public static final String ENCRYPTION_OPERATION_FAILED = "[onqlave] SDK: %s - Failed encrypting plain data";

    public static final String DECRYPTING_OPERATION = "[onqlave] SDK: %s - Decrypting cipher data";
    public static final String DECRYPTED_OPERATION = "[onqlave] SDK: %s - Decrypted cipher data: operation took %s";
    public static final String DECRYPTION_OPERATION_FAILED = "[onqlave] SDK: %s - Failed decrypting cipher data";

    public static final String CLIENT_ERROR_EXTRACTING_CONTENT = "[onqlave] SDK: %s - Failed extracting request content";
    public static final String CLIENT_ERROR_CALCULATING_DIGEST = "[onqlave] SDK: %s - Failed calculating request digest";
    public static final String CLIENT_ERROR_CALCULATING_SIGNATURE = "[onqlave] SDK: %s - Failed calculating request signature";
    public static final String CLIENT_ERROR_PORTING_REQUEST = "[onqlave] SDK: %s - Failed sending %s request";

    public static final String CLIENT_OPERATION_STARTED = "[onqlave] SDK: %s - Sending request started";
    public static final String CLIENT_OPERATION_SUCCESS = "[onqlave] SDK: %s - Sending request finished successfully: operation took %s";

    public static final String HTTP_OPERATION_STARTED = "[onqlave] SDK: %s - Http operation started";
    public static final String HTTP_OPERATION_SUCCESS = "[onqlave] SDK: %s - Http operation finished successfully: operation took %s";
}
