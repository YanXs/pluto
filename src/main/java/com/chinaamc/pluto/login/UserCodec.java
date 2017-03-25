package com.chinaamc.pluto.login;

import com.chinaamc.pluto.util.AbstractObjectCodec;
import com.chinaamc.pluto.util.Codec;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserCodec extends AbstractObjectCodec<User> {

    public static UserCodec JSON = new UserCodec(Codec.JSON);

    private static final Type USER_LIST_TYPE = new TypeToken<ArrayList<User>>() {
    }.getType();

    public UserCodec(Codec codec) {
        super(codec);
    }

    public byte[] writeUsers(List<User> users) {
        return write(users);
    }

    public List<User> readUsers(byte[] bytes) {
        return readList(bytes, USER_LIST_TYPE);
    }
}
