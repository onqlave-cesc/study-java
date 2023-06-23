package onqlave_key_manager.operations;

import onqlave_key_manager.services.CPRNGService;
import onqlave_key_manager.types.*;

import static onqlave_key_manager.types.KeyOperation.AESGCMKeyVersion;

public class AES128GCMKeyOperation implements KeyOperation {
    private final KeyFactory factory;
    private final AesGcmKeyFormat format;

    public AES128GCMKeyOperation(KeyFactory factory) {
        this.format = new AesGcmKeyFormat(16, 0);
        this.factory = factory;
    }

    @Override
    public KeyFormat getFormat() {
        return format;
    }

    @Override
    public KeyFactory getFactory() {
        return factory;
    }

}

class AesGcmKeyFormat implements KeyFormat {
    private final int keySize;

    public AesGcmKeyFormat(int keySize, int version) {
        this.keySize = keySize;
    }

    @Override
    public int getSize() {
        return keySize;
    }

}
