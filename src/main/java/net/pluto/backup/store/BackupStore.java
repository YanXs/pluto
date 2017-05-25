package net.pluto.backup.store;


import net.pluto.backup.Backup;

import java.util.List;

public interface BackupStore {

    long append(Backup backup);

    void save(List<Backup> backups);

    List<String> getBackupNames();

    List<Backup> getBackups();

    List<Backup> getBackups(long traceId);

    List<Backup> getBackups(long endMs, long lookback);

    Backup getBackup(long id);

    void removeBackup(Backup backup);
}
