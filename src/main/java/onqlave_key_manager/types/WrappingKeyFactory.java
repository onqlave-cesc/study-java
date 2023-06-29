package onqlave_key_manager.types;

public interface WrappingKeyFactory {
  Unwrapping primitive(WrappingKeyOperation operation) throws Exception;
}
