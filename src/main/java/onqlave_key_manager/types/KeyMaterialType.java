package onqlave_key_manager.types;

public enum KeyMaterialType {
    UNKNOWN_KEYMATERIAL(0),
    SYMMETRIC(1),
    ASYMMETRIC_PRIVATE(2),
    ASYMMETRIC_PUBLIC(3),
    REMOTE(4);

    private final int value;

    KeyMaterialType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static KeyMaterialType fromValue(int value) {
        return switch (value) {
            case 0 -> UNKNOWN_KEYMATERIAL;
            case 1 -> SYMMETRIC;
            case 2 -> ASYMMETRIC_PRIVATE;
            case 3 -> ASYMMETRIC_PUBLIC;
            case 4 -> REMOTE;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}
