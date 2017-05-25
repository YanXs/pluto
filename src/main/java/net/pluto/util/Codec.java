package net.pluto.util;

import java.lang.reflect.Type;

public interface Codec {

    Codec JSON = new JsonCodec();

    byte[] write(Object value);

    <T> T read(byte[] bytes, Type type);
}
