package onqlave_contracts;

public class DataDecryptionKey {
    private String wrappedDataKey;

    public DataDecryptionKey(String wrappedDataKey) {
        this.wrappedDataKey = wrappedDataKey;
    }

    public String getWrappedDataKey() {
        return wrappedDataKey;
    }

    public void setWrappedDataKey(String wrappedDataKey) {
        this.wrappedDataKey = wrappedDataKey;
    }
}
