package onqlave_key_manager.primitives;

import onqlave_key_manager.types.Unwrapping;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class RSASSAPKCS1SHA implements Unwrapping {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String CIPHER_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public byte[] unwrapKey(byte[] wdk, byte[] epk, byte[] fp, byte[] password) throws Exception {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(epk);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        RSAPrivateCrtKey rsaPrivateKey = (RSAPrivateCrtKey) privateKey;

        byte[] dk = decryptOAEP(rsaPrivateKey, wdk);

        // TODO: verify fp

        return dk;
    }

    private byte[] decryptOAEP(RSAPrivateCrtKey privateKey, byte[] ciphertext) throws Exception {
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(CIPHER_ALGORITHM, "BC");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(ciphertext);
    }

}
