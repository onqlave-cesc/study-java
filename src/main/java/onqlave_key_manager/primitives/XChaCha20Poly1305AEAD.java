package onqlave_key_manager.primitives;

import java.security.SecureRandom;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import onqlave_key_manager.types.AEAD;
import onqlave_key_manager.types.Key;

public class XChaCha20Poly1305AEAD implements AEAD {
    private final SecureRandom randomService;
    private final byte[] key;
    private final boolean prependIV;

    private static final int XChaCha20Poly1305NonceSize = 24;
    private static final int XChaCha20Poly1305TagSize = 16;
    private static final int MIN_PREPEND_IV_CIPHERTEXT_SIZE = XChaCha20Poly1305NonceSize + XChaCha20Poly1305TagSize;
    private static final int MIN_NO_IV_CIPHERTEXT_SIZE = XChaCha20Poly1305TagSize;

    public XChaCha20Poly1305AEAD(Key key, SecureRandom randomService) throws Exception {
        byte[] keyValue = key.getData().getValue();
        int keySize = keyValue.length;
        if (keySize != 32) {
            throw new IllegalArgumentException("Invalid XChaCha20Poly1305 key size; want 32, got " + keySize);
        }
        this.randomService = randomService;
        this.key = Arrays.copyOf(keyValue, keySize);
        this.prependIV = true;
    }

    @Override
    public byte[] encrypt(byte[] plaintext, byte[] associatedData) throws Exception {
        byte[] iv = newNonce();

        Cipher cipher = newCipher(Cipher.ENCRYPT_MODE, key, iv);

        byte[] ciphertext = cipher.doFinal(plaintext);

        if (prependIV) {
            byte[] ivAndCiphertext = new byte[XChaCha20Poly1305NonceSize + ciphertext.length];
            System.arraycopy(iv, 0, ivAndCiphertext, 0, XChaCha20Poly1305NonceSize);
            System.arraycopy(ciphertext, 0, ivAndCiphertext, XChaCha20Poly1305NonceSize, ciphertext.length);
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
            iv = Arrays.copyOfRange(ciphertext, 0, XChaCha20Poly1305NonceSize);
            actualCiphertext = Arrays.copyOfRange(ciphertext, XChaCha20Poly1305NonceSize, ciphertext.length);
        } else {
            if (ciphertext.length < MIN_NO_IV_CIPHERTEXT_SIZE) {
                throw new IllegalArgumentException("ciphertext too short");
            }
            iv = new byte[XChaCha20Poly1305NonceSize];
            actualCiphertext = ciphertext;
        }

        Cipher cipher = newCipher(Cipher.DECRYPT_MODE, key, iv);

        return cipher.doFinal(actualCiphertext);
    }

    private static Cipher newCipher(int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305/XChaCha20-Poly1305");
        SecretKeySpec keySpec = new SecretKeySpec(key, "ChaCha20");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(XChaCha20Poly1305TagSize * 8, iv);
        cipher.init(mode, keySpec, gcmSpec);
        return cipher;
    }

    private byte[] newNonce() {
        return randomService.generateSeed(XChaCha20Poly1305NonceSize);
    }
}
