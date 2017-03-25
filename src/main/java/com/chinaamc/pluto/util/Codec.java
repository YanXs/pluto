package com.chinaamc.pluto.util;

import com.chinaamc.pluto.util.JsonCodec;

import java.lang.reflect.Type;

public interface Codec {

    JsonCodec JSON = new JsonCodec();

    byte[] write(Object value);

    <T> T read(byte[] bytes, Type type);
}
