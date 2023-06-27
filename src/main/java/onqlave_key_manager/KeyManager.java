package onqlave_key_manager;

import org.javatuples.Triplet;

public interface KeyManager {
  Triplet<byte[], byte[], String> fetchEncryptionKey() throws Exception;
  byte[] fetchDecryptionKey(byte[] edk) throws Exception;
}
