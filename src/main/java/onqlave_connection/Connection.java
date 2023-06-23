package onqlave_connection;

import java.security.InvalidKeyException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import onqlave_contracts.OnqlaveRequest;
import onqlave_errors.OnqlaveError;
import onqlave_logger.CustomLogger;


import static onqlave_connection.OnqlaveConnection.*;
import static onqlave_errors.ErrorCodes.Server;
import static onqlave_messages.Messages.*;


class Configuration {
    public final Credential credential;
    public final RetrySettings retry;
    public final String arxURL;
    public final String arxID;

    public Configuration(Credential credential, RetrySettings retry, String arxURL, String arxID) {
        this.credential = credential;
        this.retry = retry;
        this.arxURL = arxURL;
        this.arxID = arxID;
    }
}

class Credential {
    public final String accessKey;
    public final String signingKey;

    public Credential(String accessKey, String signingKey) {
        this.accessKey = accessKey;
        this.signingKey = signingKey;
    }
}

public class RetrySettings {
    public final int maxRetries;
    public final int retryDelay;

    public RetrySettings(int maxRetries, int retryDelay) {
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }
}


public interface Connection {
    byte[] post(String resource, OnqlaveRequest body) throws OnqlaveError;
}

class OnqlaveConnection implements Connection {
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
        this.client = new Client(configuration.retry, logger);
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
        } catch (NoSuchAlgorithmException | IOException e) {
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
        } catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
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
        }
        return response;
    }
}

class Client {
    private final RetrySettings retry;
    private final CustomLogger logger;

    public Client(RetrySettings retry, CustomLogger logger) {
        this.retry = retry;
        this.logger = logger;
    }

    public byte[] post(String urlString, OnqlaveRequest body, Map<String, String> headers) throws IOException, OnqlaveError {
        int retries = 0;
        while (true) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setConnectTimeout(retry.retryDelay);
                conn.setReadTimeout(retry.retryDelay);
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
                OutputStream os = conn.getOutputStream();
                os.write(body.getContent());
                os.flush();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString().getBytes(StandardCharsets.UTF_8);
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JsonObject errorJson = JsonParser.parseString(response.toString()).getAsJsonObject();
                    throw new OnqlaveError(Server, errorJson.get("message").getAsString());
                }
            } catch (IOException | OnqlaveError e) {
                if (++retries >= retry.maxRetries) {
                    throw e;
                }
                try {
                    Thread.sleep(retry.retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}

class Hasher {
    private static final String SHA_256 = "SHA-256";
    private static final String HMAC_SHA_256 = "HmacSHA256";

    public String digest(OnqlaveRequest request) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(SHA_256);
        md.update(request.getContent());
        byte[] digest = md.digest();
        return Base64.getEncoder().encodeToString(digest);
    }

    public String sign(Map<String, String> headers, String signingKey) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
        String stringToSign = String.valueOf(getStringToSign(headers));
        Mac mac = Mac.getInstance(HMAC_SHA_256);
        SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
        mac.init(secretKeySpec);
        byte[] signature = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature);
    }

    private StringBuilder getStringToSign(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append(headers.get(ONQLAVE_API_KEY)).append("\n");
        sb.append(headers.get(ONQLAVE_ARX)).append("\n");
        sb.append(headers.get(ONQLAVE_CONTENT_LENGTH)).append("\n");
        sb.append(headers.get(ONQLAVE_CONTENT)).append("\n");
        sb.append(headers.get(ONQLAVE_DIGEST)).append("\n");
        sb.append(headers.get(ONQLAVE_HOST)).append("\n");
        return sb;
    }
}
