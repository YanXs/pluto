package com.chinaamc.pluto.backup;

import java.util.List;

public class DefaultBackupCodec implements BackupCodec {

    public static final BackupCodec JSON = new DefaultBackupCodec(Codec.JSON);

    private final Codec codec;

    private DefaultBackupCodec(Codec codec) {
        this.codec = codec;
    }

    @Override
    public byte[] writeBackup(Backup backup) {
        return codec.writeBackup(backup);
    }

    @Override
    public byte[] writeBackups(List<Backup> backups) {
        return codec.writeBackups(backups);
    }

    @Override
    public Backup readBackup(byte[] bytes) {
        return codec.readBackup(bytes);
    }

    @Override
    public List<Backup> readBackups(byte[] bytes) {
        return codec.readBackups(bytes);
    }
}
