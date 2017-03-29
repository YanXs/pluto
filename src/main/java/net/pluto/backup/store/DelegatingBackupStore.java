package net.pluto.backup.store;

import net.pluto.backup.Backup;

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
