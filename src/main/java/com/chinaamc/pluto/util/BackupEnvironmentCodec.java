package com.chinaamc.pluto.util;

import com.chinaamc.pluto.backup.BackupEnvironment;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BackupEnvironmentCodec extends AbstractObjectCodec<BackupEnvironment> {

    private static final Type BACKUP_LIST_TYPE = new TypeToken<ArrayList<BackupEnvironment>>() {
    }.getType();

    public BackupEnvironmentCodec(Codec codec) {
        super(codec);
    }

    public List<BackupEnvironment> readBackupEnvironments(byte[] bytes) {
        return readList(bytes, BACKUP_LIST_TYPE);
    }
}
