package com.chinaamc.pluto.test;

import com.chinaamc.pluto.backup.Backup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JsonCodecTest {

    private final Random random = new Random();

    @Test
    public void test_gson_serializable() throws Exception {
        Backup.Builder builder = new Backup.Builder();
        builder
                .traceId(random.nextLong())
                .id(random.nextLong())
                .parentId(random.nextLong())
                .backupType(0)
                .timestamp(System.currentTimeMillis())
                .backupDirectory("/test");
        Backup backup = builder.build();
        List<Backup> list = new ArrayList<>();
        list.add(backup);
        list.add(backup);
        Gson gson = new Gson();
        String json = gson.toJson(list);
        System.out.println(json);
        FileUtils.writeByteArrayToFile(new File("test.data"), json.getBytes("UTF-8"));

        Type type = new TypeToken<ArrayList<Backup>>() {
        }.getType();
        list = gson.fromJson(new String(FileUtils.readFileToByteArray(new File("test.data"))), type);
        System.out.println(list.size());
    }
}
