package onqlave_key_manager.operations;

import onqlave_key_manager.types.KeyFormat;
import onqlave_key_manager.types.WrappingKeyFactory;
import onqlave_key_manager.types.WrappingKeyOperation;
import onqlave_key_manager.types.HashType;

public class rsaSSAPKCS1SHA2562048KeyOperation implements WrappingKeyOperation {
  private final int RSASSAPKCS1KeyVersion = 0;
  public WrappingKeyFactory factory;
  public RsaSsaPkcs1KeyFormat format;

  public rsaSSAPKCS1SHA2562048KeyOperation(WrappingKeyFactory factory) {
    RsaSsaPkcs1KeyFormat format = new RsaSsaPkcs1KeyFormat(RSASSAPKCS1KeyVersion);
    this.factory = factory;
    this.format = format;
  }

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
  public HashType hash;

  public RsaSsaPkcs1KeyFormat(int version) {
    this.version = version;
    this.hash = HashType.fromValue(3);
  }

  @Override
  public int getSize() {
    return 0;
  }
}
