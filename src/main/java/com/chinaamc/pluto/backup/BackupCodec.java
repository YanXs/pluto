package com.chinaamc.pluto.backup;

import com.chinaamc.pluto.util.AbstractObjectCodec;
import com.chinaamc.pluto.util.Codec;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BackupCodec extends AbstractObjectCodec<Backup> {

    public static BackupCodec JSON = new BackupCodec(Codec.JSON);

    private static final Type BACKUP_LIST_TYPE = new TypeToken<ArrayList<Backup>>() {
    }.getType();


    private BackupCodec(Codec codec) {
        super(codec);
    }

    public byte[] writeBackup(Backup backup) {
        return write(backup);
    }

    public byte[] writeBackups(List<Backup> backups) {
        return write(backups);
    }

    public Backup readBackup(byte[] bytes) {
        return read(bytes, Backup.class);
    }

    public List<Backup> readBackups(byte[] bytes) {
        return readList(bytes, BACKUP_LIST_TYPE);
    }
}
