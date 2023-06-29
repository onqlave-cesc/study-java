package onqlave_key_manager.types;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Map;

interface TypeResolver {
    byte[] serialise(String name, Object input) throws Exception;
    Object deserialise(String name, byte[] input) throws Exception;
}

class TypeResolverImpl implements TypeResolver {
    @Override
    public byte[] serialise(String name, Object input) throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
        Class<?> clazz = input.getClass();
        buffer.put((byte) clazz.getTypeName().hashCode());
        switch (clazz.getTypeName()) {
            case "boolean" -> buffer.put((byte) ((boolean) input ? 1 : 0));
            case "byte" -> buffer.put((byte) input);
            case "short" -> buffer.putShort((short) input);
            case "int" -> buffer.putInt((int) input);
            case "long" -> buffer.putLong((long) input);
            case "float" -> buffer.putFloat((float) input);
            case "double" -> buffer.putDouble((double) input);
            case "java.lang.String" -> {
                byte[] strBytes = ((String) input).getBytes();
                buffer.putInt(strBytes.length);
                buffer.put(strBytes);
            }
            case "java.time.Instant" -> buffer.putLong(((Instant) input).getEpochSecond());
            case "[B" -> {
                byte[] bytes = (byte[]) input;
                buffer.putInt(bytes.length);
                buffer.put(bytes);
            }
            default -> throw new Exception(name + ": unsupported type: " + clazz.getTypeName());
        }
        buffer.flip();
        byte[] serialised = new byte[buffer.remaining()];
        buffer.get(serialised);
        return serialised;
    }

    @Override
    public Object deserialise(String name, byte[] input) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(input).order(ByteOrder.BIG_ENDIAN);
        byte kind = buffer.get();
        switch (kind) {
            case -1 -> {
                return null;
            }
            case 1 -> {
                return buffer.get() == 1;
            }
            case 2 -> {
                return buffer.get();
            }
            case 3 -> {
                return buffer.getShort();
            }
            case 4 -> {
                return buffer.getInt();
            }
            case 5 -> {
                return buffer.getLong();
            }
            case 6 -> {
                return buffer.getFloat();
            }
            case 7 -> {
                return buffer.getDouble();
            }
            case 8 -> {
                int strBytesLen = buffer.getInt();
                byte[] strBytes = new byte[strBytesLen];
                buffer.get(strBytes);
                return new String(strBytes);
            }
            case 9 -> {
                return Instant.ofEpochSecond(buffer.getLong());
            }
            case 10 -> {
                int bytesLen = buffer.getInt();
                byte[] bytes = new byte[bytesLen];
                buffer.get(bytes);
                return bytes;
            }
            default -> throw new Exception(name + ": unsupported type: " + kind);
        }
    }
}

class OnqlaveStructure {
    private Map<String, byte[]> embeded;
    private byte[] edk;
    //constructor, getters, and setters
}

enum AlgorithmType {
    UNKNOWN_ALGORITHM(0),
    AES_GCM_128(1),
    AES_GCM_256(2),
    XCHA_CHA_20_POLY_1305(3);

    private int value;

    AlgorithmType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AlgorithmType fromValue(int value) {
        return switch (value) {
            case 0 -> UNKNOWN_ALGORITHM;
            case 1 -> AES_GCM_128;
            case 2 -> AES_GCM_256;
            case 3 -> XCHA_CHA_20_POLY_1305;
            default -> throw new IllegalArgumentException("Invalid value: " + value);
        };
    }
}
class Algorithm implements AlgorithmSeriliser, AlogorithmDeserialiser {
    private byte version;
    private byte algo;
    private byte[] key;

    public Algorithm(byte version, byte algo, byte[] key) {
        this.version = version;
        this.algo = algo;
        this.key = key;
    }

    public Algorithm() {
    }

    @Override
    public byte[] serialise() throws Exception {
        ByteBuffer buffer = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(7 + key.length);
        buffer.put(version);
        buffer.put(algo);
        buffer.put((byte) key.length);
        buffer.put(key);
        buffer.flip();
        byte[] serialised = new byte[buffer.remaining()];
        buffer.get(serialised);
        return serialised;
    }

    @Override
    public int deserialise(byte[] buffer) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN);
        int headerLen = bb.getInt();
        version = bb.get();
        algo = bb.get();
        byte keyLen = bb.get();
        key = new byte[keyLen];
        bb.get(key);
        return headerLen;
    }

    public byte[] getKey() {
        return key;
    }
    public byte getVersion() {
        return version;
    }

    @Override
    public AlgorithmType getAlgorithm() {
        return AlgorithmType.fromValue(algo);
    }


}

interface AlgorithmSeriliser {
    byte[] serialise() throws Exception;
}

interface AlogorithmDeserialiser {
    int deserialise(byte[] buffer) throws Exception;
    byte[] getKey();
    byte getVersion();
    AlgorithmType getAlgorithm();
}
