package onqlave_key_manager.types;

public enum HashType {
    UNKNOWN_HASH(0),
    SHA1(1),
    SHA384(2),
    SHA256(3),
    SHA512(4),
    SHA224(5);

    private int value;

    HashType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static HashType fromValue(int value) {
        return switch (value) {
            case 0 -> UNKNOWN_HASH;
            case 1 -> SHA1;
            case 2 -> SHA384;
            case 3 -> SHA256;
            case 4 -> SHA512;
            case 5 -> SHA224;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}
