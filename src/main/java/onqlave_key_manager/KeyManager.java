package onqlave_key_manager;

import java.util.*;

public interface KeyManager {
  List<Object> fetchEncryptionKey() throws Exception;
  List<Object> fetchDecryptionKey(byte[] edk) throws Exception;
}
