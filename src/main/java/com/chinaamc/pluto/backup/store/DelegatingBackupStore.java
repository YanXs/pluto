package com.chinaamc.pluto.backup.store;

import com.chinaamc.pluto.backup.Backup;
import com.chinaamc.pluto.backup.Codec;
import com.chinaamc.pluto.util.Configuration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DelegatingBackupStore implements BackupStore {

    private final BackupStore inMemoryBackupStore;

    private final BackupStore persistBackupStore;

    public DelegatingBackupStore(BackupStore persistBackupStore) {
        this.persistBackupStore = persistBackupStore;
        inMemoryBackupStore = new InMemoryBackupStore(persistBackupStore);
    }

    @Override
    public long append(Backup backup) {
        inMemoryBackupStore.append(backup);
        List<Backup> backups = inMemoryBackupStore.getBackups();
        save(backups);
        backup = backups.get(backups.size() - 1);
        return backup.getTraceId();
    }

    @Override
    public void save(List<Backup> backups) {
        persistBackupStore.save(backups);
    }

    @Override
    public List<String> getBackupNames() {
        return inMemoryBackupStore.getBackupNames();
    }

    @Override
    public List<Backup> getBackups() {
        return inMemoryBackupStore.getBackups();
    }

    @Override
    public List<Backup> getBackups(long traceId) {
        return inMemoryBackupStore.getBackups(traceId);
    }

    @Override
    public List<Backup> getBackups(long endMs, long lookback) {
        return inMemoryBackupStore.getBackups(endMs, lookback);
    }

    @Override
    public Backup getBackup(long id) {
        return inMemoryBackupStore.getBackup(id);
    }

    @Override
    public void removeBackup(Backup backup) {
        inMemoryBackupStore.removeBackup(backup);
        persistBackupStore.removeBackup(backup);
        save(getBackups());
    }
}
