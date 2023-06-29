package onqlave_key_manager.operations;

import onqlave_key_manager.types.KeyFormat;
import onqlave_key_manager.types.WrappingKeyFactory;
import onqlave_key_manager.types.WrappingKeyOperation;
import onqlave_key_manager.types.HashType;

public class rsaSSAPKCS1SHA2562048KeyOperation implements WrappingKeyOperation {
  public WrappingKeyFactory factory;
  public RsaSsaPkcs1KeyFormat format;

  @Override
  public KeyFormat getFormat() {
    return this.format;
  }

  @Override
  public WrappingKeyFactory getFactory() {
    return this.factory;
  }
}

class RsaSsaPkcs1KeyFormat implements KeyFormat {
  private final int version;
  public HashType Hash;

  public RsaSsaPkcs1KeyFormat(int version) {
    this.version = version;
  }

  @Override
  public int getSize() {
    return 0;
  }
}
