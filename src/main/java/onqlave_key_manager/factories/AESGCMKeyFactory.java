package onqlave_key_manager.factories;

import onqlave_key_manager.keys.AesGcmKey;
import onqlave_key_manager.keys.AesGcmKeyData;
import onqlave_key_manager.primitives.AESGCMAEAD;
import onqlave_key_manager.services.CPRNGService;
import onqlave_key_manager.services.IDService;
import onqlave_key_manager.types.*;

import static onqlave_key_manager.types.KeyOperation.AESGCMKeyVersion;

public class AESGCMKeyFactory implements KeyFactory {

    private final IDService idService;
    private final CPRNGService randomService;

    public AESGCMKeyFactory(IDService idService, CPRNGService randomService) {
        this.idService = idService;
        this.randomService = randomService;
    }

    @Override
    public Key newKey(KeyOperation operation) throws Exception {
        KeyFormat format = operation.getFormat();
        validateKeyFormat(format);
        byte[] keyValue = randomService.getRandomBytes(format.getSize());
        AesGcmKeyData keyData = new AesGcmKeyData("",keyValue, KeyMaterialType.SYMMETRIC, 0);
        return new AesGcmKey(idService.newKeyID(), operation, keyData);
    }

    @Override
    public Key newKeyFromData(KeyOperation operation, byte[] keyData) throws Exception {
        KeyFormat format = operation.getFormat();
        validateKeyFormat(format);
        return new AesGcmKey(idService.newKeyID(), operation, new AesGcmKeyData("",keyData, KeyMaterialType.SYMMETRIC, 0));
    }

    @Override
    public AEAD primitive(Key key) throws Exception {
        validateKey(key);
        return new AESGCMAEAD(key, randomService);
    }

    private void validateKey(Key key) throws Exception {
        KeyData keyData = key.getData();
        validateKeyVersion(keyData.getVersion(), AESGCMKeyVersion);
        byte[] value = keyData.getValue();
        int keySize = value.length;
        AESGCMAEAD.ValidateAESKeySize(keySize);
    }

    private void validateKeyFormat(KeyFormat format) throws Exception {
        if (!(format instanceof AesGcmKeyFormat)) {
            throw new Exception("Invalid key format");
        }
        AesGcmKeyFormat aesGcmKeyFormat = (AesGcmKeyFormat) format;
        int keySize = aesGcmKeyFormat.getSize();
        AESGCMAEAD.ValidateAESKeySize(keySize);
    }

    private void validateKeyVersion(int version, int expectedVersion) throws Exception {
        if (version != expectedVersion) {
            throw new Exception("Invalid key version");
        }
    }
}

class AesGcmKeyFormat implements KeyFormat {

    private final int keySize;

    public AesGcmKeyFormat(int keySize) throws Exception {
        AESGCMAEAD.ValidateAESKeySize(keySize);
        this.keySize = keySize;
    }

    public int getSize() {
        return keySize;
    }

}
