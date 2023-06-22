package onqlave_key_manager.primitives;

import onqlave_key_manager.types.AEAD;
import onqlave_key_manager.types.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESGCMAEAD implements AEAD {
    private final SecureRandom randomService;
    private final byte[] key;
    private final boolean prependIV;

    private static final int AESGCMIVSize = 12;
    private static final int AESGCMTagSize = 16;
    private static final int MIN_PREPEND_IV_CIPHERTEXT_SIZE = AESGCMIVSize + AESGCMTagSize;
    private static final int MIN_NO_IV_CIPHERTEXT_SIZE = AESGCMTagSize;

    public AESGCMAEAD(Key key, SecureRandom randomService) throws Exception {
        byte[] keyValue = key.getData().getValue();
        int keySize = keyValue.length;
        if (keySize != 16 && keySize != 32) {
            throw new IllegalArgumentException("Invalid AES key size; want 16 or 32, got " + keySize);
        }
        this.randomService = randomService;
        this.key = Arrays.copyOf(keyValue, keySize);
        this.prependIV = true;
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws Exception {
        byte[] iv = randomService.generateSeed(AESGCMIVSize);

        Cipher cipher = newCipher(Cipher.ENCRYPT_MODE, key, iv);

        byte[] ciphertext = cipher.doFinal(plaintext);

        if (prependIV) {
            byte[] ivAndCiphertext = new byte[AESGCMIVSize + ciphertext.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, AESGCMIVSize);
            System.arraycopy(ciphertext, 0, ivAndCiphertext, AESGCMIVSize, ciphertext.length);
            return ivAndCiphertext;
        } else {
            return ciphertext;
        }
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws Exception {
        byte[] iv;
        byte[] actualCiphertext;

        if (prependIV) {
            if (ciphertext.length < MIN_PREPEND_IV_CIPHERTEXT_SIZE) {
                throw new IllegalArgumentException("ciphertext too short");
            }
            iv = Arrays.copyOfRange(ciphertext, 0, AESGCMIVSize);
            actualCiphertext = Arrays.copyOfRange(ciphertext, AESGCMIVSize, ciphertext.length);
        } else {
            if (ciphertext.length < MIN_NO_IV_CIPHERTEXT_SIZE) {
                throw new IllegalArgumentException("ciphertext too short");
            }
            iv = new byte[AESGCMIVSize];
            actualCiphertext = ciphertext;
        }

        Cipher cipher = newCipher(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(actualCiphertext);
    }

    private static Cipher newCipher(int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(AESGCMTagSize * 8, iv);
        cipher.init(mode, keySpec, gcmSpec);
        return cipher;
    }
}
