package net.pluto.backup;

import net.pluto.exceptions.CodecException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonCodec implements Codec {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Type type = new TypeToken<ArrayList<Backup>>() {
    }.getType();

    @Override
    public Backup readBackup(byte[] bytes) {
        return gson.fromJson(new String(bytes), Backup.class);
    }

    @Override
    public byte[] writeBackup(Backup value) {
        return doWriteJson(value);
    }

    @Override
    public List<Backup> readBackups(byte[] bytes) {
        return gson.fromJson(new String(bytes), type);
    }

    @Override
    public byte[] writeBackups(List<Backup> value) {
        return doWriteJson(value);
    }

    private byte[] doWriteJson(Object value) {
        String json = gson.toJson(value);
        try {
            return json.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new CodecException(e);
        }
    }
}
