package com.chinaamc.pluto.backup;

import java.util.List;

public interface BackupCodec {

    BackupCodec JSON = DefaultBackupCodec.JSON;

    byte[] writeBackup(Backup backup);

    byte[] writeBackups(List<Backup> backups);

    Backup readBackup(byte[] bytes);

    List<Backup> readBackups(byte[] bytes);
}
