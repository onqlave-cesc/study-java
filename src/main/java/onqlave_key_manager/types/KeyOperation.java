package onqlave_key_manager.types;


public interface KeyOperation {

    KeyFormat getFormat();
    KeyFactory getFactory();
    int AESGCMKeyVersion = 0;
}
