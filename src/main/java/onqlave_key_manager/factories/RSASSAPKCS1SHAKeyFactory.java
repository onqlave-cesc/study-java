package onqlave_key_manager.factories;

import onqlave_key_manager.services.CPRNGService;
import onqlave_key_manager.types.KeyFormat;
import onqlave_key_manager.types.Unwrapping;
import onqlave_key_manager.types.WrappingKeyFactory;
import onqlave_key_manager.types.WrappingKeyOperation;

public class RSASSAPKCS1SHAKeyFactory implements WrappingKeyFactory{
  private final CPRNGService randomService;

  public RSASSAPKCS1SHAKeyFactory(CPRNGService randomService) {
    this.randomService = randomService;
  }

  @Override
  public Unwrapping primitive(WrappingKeyOperation operation) throws Exception {
    KeyFormat format = operation.getFormat();

    return null;
  }
}
