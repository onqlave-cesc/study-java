package onqlave_key_manager.types;

public interface WrappingKeyOperation {
  KeyFormat getFormat();
  WrappingKeyFactory getFactory();
}
