package onqlave_connection;


import java.util.HashMap;
import java.util.Date;
import java.security.InvalidKeyException;
import java.util.Map;
import java.security.NoSuchAlgorithmException;
import java.io.IOException;

import onqlave_contracts.OnqlaveRequest;
import onqlave_errors.OnqlaveError;
import onqlave_logger.CustomLogger;
import onqlave_uitls.*;

import static onqlave_errors.ErrorCodes.Server;
import static onqlave_messages.Messages.*;

public class OnqlaveConnection implements Connection {
    static final String ONQLAVE_API_KEY = "ONQLAVE-API-KEY";
    static final String ONQLAVE_CONTENT = "Content-Type";
    static final String ONQLAVE_HOST = "ONQLAVE-HOST";
    private static final String ONQLAVE_VERSION = "ONQLAVE-VERSION";
    private static final String ONQLAVE_SIGNATURE = "ONQLAVE-SIGNATURE";
    static final String ONQLAVE_DIGEST = "ONQLAVE-DIGEST";
    static final String ONQLAVE_ARX = "ONQLAVE-ARX";
    private static final String ONQLAVE_AGENT = "User-Agent";
    private static final String ONQLAVE_REQUEST_TIME = "ONQLAVE-REQUEST-TIME";
    static final String ONQLAVE_CONTENT_LENGTH = "ONQLAVE-CONTEXT-LEN";
    private static final String SERVER_TYPE = "Onqlave/0.1";
    private static final String VERSION = "0.1";
    private static final String ONQLAVE_CONTENT_TYPE = "application/json";

    private final Client client;
    private final Hasher hasher;
    private final CustomLogger logger;
    private final Configuration configuration;

    public OnqlaveConnection(Configuration configuration, Hasher hasher, CustomLogger logger) {
        this.client = new ClientRequest(configuration.retry, logger);
        this.hasher = hasher;
        this.logger = logger;
        this.configuration = configuration;
    }

    @Override
    public byte[] post(String resource, OnqlaveRequest body) throws OnqlaveError {
        String operation = "Post";
        long start = System.currentTimeMillis();
        String urlString = String.format("%s/%s", configuration.arxURL, resource);
        String arxID = configuration.arxID;
        Date now = new Date();
        byte[] content;
        try {
            content = body.getContent();
        } catch (IOException e) {
            throw new OnqlaveError(Server, String.format(CLIENT_ERROR_EXTRACTING_CONTENT, operation), e);
        }
        int contentLen = content.length;
        String digest;
        try {
            digest = hasher.digest(body);
        } catch (Exception e) {
            throw new OnqlaveError(Server, String.format(CLIENT_ERROR_CALCULATING_DIGEST, operation), e);
        }
        Map<String, String> headersToSign = new HashMap<>();
        headersToSign.put(ONQLAVE_API_KEY, configuration.credential.accessKey);
        headersToSign.put(ONQLAVE_ARX, arxID);
        headersToSign.put(ONQLAVE_HOST, configuration.arxURL);
        headersToSign.put(ONQLAVE_AGENT, SERVER_TYPE);
        headersToSign.put(ONQLAVE_CONTENT_LENGTH, Integer.toString(contentLen));
        headersToSign.put(ONQLAVE_DIGEST, digest);
        headersToSign.put(ONQLAVE_VERSION, VERSION);
        String signature;
        try {
            signature = hasher.sign(headersToSign, configuration.credential.signingKey);
        } catch (Exception e) {
            throw new OnqlaveError(Server, String.format(CLIENT_ERROR_CALCULATING_SIGNATURE, operation), e);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(ONQLAVE_CONTENT, ONQLAVE_CONTENT_TYPE);
        headers.put(ONQLAVE_API_KEY, configuration.credential.accessKey);
        headers.put(ONQLAVE_ARX, arxID);
        headers.put(ONQLAVE_HOST, configuration.arxURL);
        headers.put(ONQLAVE_AGENT, SERVER_TYPE);
        headers.put(ONQLAVE_REQUEST_TIME, Long.toString(now.getTime()));
        headers.put(ONQLAVE_CONTENT_LENGTH, Integer.toString(contentLen));
        headers.put(ONQLAVE_DIGEST, digest);
        headers.put(ONQLAVE_VERSION, VERSION);
        headers.put(ONQLAVE_SIGNATURE, signature);
        byte[] response;
        try {
            response = client.post(urlString, body, headers);
        } catch (IOException e) {
            throw new OnqlaveError(Server, String.format(CLIENT_ERROR_PORTING_REQUEST, operation, "HTTP:POST"), e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
