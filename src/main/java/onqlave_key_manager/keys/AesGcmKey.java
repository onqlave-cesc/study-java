package onqlave_key_manager.keys;


import onqlave_key_manager.types.*;

public class AesGcmKey implements Key {
    private final KeyID keyID;
    private final KeyOperation operation;
    private final AesGcmKeyData data;

    public AesGcmKey(KeyID keyID, KeyOperation operation, AesGcmKeyData data) {
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


