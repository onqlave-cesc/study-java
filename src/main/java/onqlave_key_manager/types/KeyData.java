package onqlave_key_manager.types;

public interface KeyData {
    byte[] getValue() throws Exception;

    void fromValue(byte[] data) throws Exception;

    String getTypeURL();

    KeyMaterialType getKeyMaterialType();

    long getVersion();
}
