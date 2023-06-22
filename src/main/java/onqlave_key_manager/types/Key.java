package onqlave_key_manager.types;

public interface Key {
    KeyID getKeyID();

    KeyOperation getOperation();

    KeyData getData();
}
