package onqlave_key_manager.keys;

import onqlave_key_manager.types.KeyData;
import onqlave_key_manager.types.KeyMaterialType;

public class AesGcmKeyData implements KeyData {
    private final String typeURL;
    private final byte[] value;
    private final KeyMaterialType keyMaterialType;
    private final int version;

    public AesGcmKeyData(String typeURL, byte[] value, KeyMaterialType keyMaterialType, int version) {
        this.typeURL = typeURL;
        this.value = value;
        this.keyMaterialType = keyMaterialType;
        this.version = version;
    }

    @Override
    public byte[] getValue() {
        return value;
    }

    @Override
    public void fromValue(byte[] data) {
    }

    @Override
    public String getTypeURL() {
        return typeURL;
    }

    @Override
    public KeyMaterialType getKeyMaterialType() {
        return keyMaterialType;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
