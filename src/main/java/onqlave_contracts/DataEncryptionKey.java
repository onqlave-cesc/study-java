package onqlave_contracts;

public class DataEncryptionKey {
    private String encryptedDataKey;
    private String wrappedDataKey;

    public DataEncryptionKey(String encryptedDataKey, String wrappedDataKey) {
        this.encryptedDataKey = encryptedDataKey;
        this.wrappedDataKey = wrappedDataKey;
    }

    public String getEncryptedDataKey() {
        return encryptedDataKey;
    }

    public void setEncryptedDataKey(String encryptedDataKey) {
        this.encryptedDataKey = encryptedDataKey;
    }

    public String getWrappedDataKey() {
        return wrappedDataKey;
    }

    public void setWrappedDataKey(String wrappedDataKey) {
        this.wrappedDataKey = wrappedDataKey;
    }
}
