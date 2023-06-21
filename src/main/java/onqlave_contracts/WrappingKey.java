package onqlave_contracts;

public class WrappingKey {
    private String encryptedPrivateKey;
    private String keyFingerprint;

    public WrappingKey(String encryptedPrivateKey, String keyFingerprint) {
        this.encryptedPrivateKey = encryptedPrivateKey;
        this.keyFingerprint = keyFingerprint;
    }

    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    public void setEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

    public String getKeyFingerprint() {
        return keyFingerprint;
    }

    public void setKeyFingerprint(String keyFingerprint) {
        this.keyFingerprint = keyFingerprint;
    }
}
