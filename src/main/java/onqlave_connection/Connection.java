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

public interface Connection {
    byte[] post(String resource, OnqlaveRequest body) throws OnqlaveError;
}

class ClientRequest implements Client {
    private final RetrySettings retry;
    private final CustomLogger logger;

    public ClientRequest(RetrySettings retry, CustomLogger logger) {
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

// class Hasher {
//     private static final String SHA_256 = "SHA-256";
//     private static final String HMAC_SHA_256 = "HmacSHA256";

//     public String digest(OnqlaveRequest request) throws NoSuchAlgorithmException, IOException {
//         MessageDigest md = MessageDigest.getInstance(SHA_256);
//         md.update(request.getContent());
//         byte[] digest = md.digest();
//         return Base64.getEncoder().encodeToString(digest);
//     }

//     public String sign(Map<String, String> headers, String signingKey) throws NoSuchAlgorithmException, IOException, InvalidKeyException {
//         String stringToSign = String.valueOf(getStringToSign(headers));
//         Mac mac = Mac.getInstance(HMAC_SHA_256);
//         SecretKeySpec secretKeySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);
//         mac.init(secretKeySpec);
//         byte[] signature = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
//         return Base64.getEncoder().encodeToString(signature);
//     }

//     private StringBuilder getStringToSign(Map<String, String> headers) {
//         StringBuilder sb = new StringBuilder();
//         sb.append(headers.get(ONQLAVE_API_KEY)).append("\n");
//         sb.append(headers.get(ONQLAVE_ARX)).append("\n");
//         sb.append(headers.get(ONQLAVE_CONTENT_LENGTH)).append("\n");
//         sb.append(headers.get(ONQLAVE_CONTENT)).append("\n");
//         sb.append(headers.get(ONQLAVE_DIGEST)).append("\n");
//         sb.append(headers.get(ONQLAVE_HOST)).append("\n");
//         return sb;
//     }
// }
