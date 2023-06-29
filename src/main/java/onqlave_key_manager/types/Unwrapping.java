package onqlave_key_manager.types;

public interface Unwrapping {
  byte[] unwrapKey(byte[] wdk, byte[] epk, byte[] fp, byte[] password) throws Exception;
}
