package onqlave_key_manager.keys;


import onqlave_key_manager.types.*;

public class XChaCha20Poly1305Key implements Key {
    private final KeyID keyID;
    private final KeyOperation operation;
    private final XChaCha20Poly1305KeyData data;

    public XChaCha20Poly1305Key(KeyID keyID, KeyOperation operation, XChaCha20Poly1305KeyData data) {
        this.keyID = keyID;
        this.operation = operation;
        this.data = data;
    }

    @Override
    public KeyID getKeyID() {
        return keyID;
    }

    @Override
    public KeyOperation getOperation() {
        return operation;
    }

    @Override
    public KeyData getData() {
        return data;
    }
}

class XChaCha20Poly1305KeyData implements KeyData {
    private final String typeURL;
    private final byte[] value;
    private final KeyMaterialType keyMaterialType;
    private final int version;

    public XChaCha20Poly1305KeyData(String typeURL, byte[] value, KeyMaterialType keyMaterialType, int version) {
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
    public void fromValue(byte[] data) throws Exception {
        if (data.length != value.length) {
            throw new Exception("Invalid key data");
        }
        System.arraycopy(data, 0, value, 0, value.length);
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

