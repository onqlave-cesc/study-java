package onqlave_key_manager.types;

public interface AEAD {
    byte[] encrypt(byte[] plaintext, byte[] associatedData) throws Exception;

    byte[] decrypt(byte[] ciphertext, byte[] associatedData) throws Exception;
}
