package onqlave_uitls;

import onqlave_contracts.OnqlaveRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class HasherImpl implements Hasher {
    private static final String DIGEST_ALGORITHM = "SHA-512";
    private static final String SIGNATURE_ALGORITHM = "HmacSHA512";

    public String digest(OnqlaveRequest body) throws Exception {//FIXME why have hash and sign?
        byte[] content = body.getContent();
        if (content == null) {
            throw new Exception("Request body content is null");
        }
        byte[] digestBytes = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(content);
        return "SHA512=" + Base64.getEncoder().encodeToString(digestBytes);
    }

    public String sign(Map<String, String> headers, String signingKey) throws Exception {
        Mac mac = Mac.getInstance(SIGNATURE_ALGORITHM);
        SecretKeySpec signingKeySpec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), SIGNATURE_ALGORITHM);
        mac.init(signingKeySpec);

        Map<String, String> sortedHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        sortedHeaders.putAll(headers);

        StringBuilder signatureBuilder = new StringBuilder();
        for (Map.Entry<String, String> header : sortedHeaders.entrySet()) {
            String headerName = header.getKey().toLowerCase();
            String headerValue = header.getValue();
            if (headerValue != null && !headerValue.isEmpty()) {
                signatureBuilder.append(headerName).append(":").append(headerValue).append("\n");
            }
        }

        byte[] signatureBytes = mac.doFinal(signatureBuilder.toString().getBytes(StandardCharsets.UTF_8));
        return "HMAC-SHA512=" + Base64.getEncoder().encodeToString(signatureBytes);
    }
}
