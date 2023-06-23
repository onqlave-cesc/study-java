package onqlave_credentials;

import java.util.Objects;

public class Credential {
    private final String accessKey;
    private final String signingKey;
    private final String secretKey;

    public Credential(String accessKey, String signingKey, String secretKey) {
        this.accessKey = Objects.requireNonNull(accessKey, "accesskey is not valid");
        this.signingKey = Objects.requireNonNull(signingKey, "signingkey is not valid");
        this.secretKey = Objects.requireNonNull(secretKey, "secretkey is not valid");
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSigningKey() {
        return signingKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void Valid() throws Exception {
        if (accessKey.isEmpty()) {
            throw new Exception("accesskey is not valid");
        }
        if (secretKey.isEmpty()) {
            throw new Exception("secretkey is not valid");
        }
        if (signingKey.isEmpty()) {
            throw new Exception("signingkey is not valid");
        }
    }
}
