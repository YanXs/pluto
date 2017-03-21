package com.chinaamc.pluto.backup.store;

import com.chinaamc.pluto.backup.Backup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractBackupStore implements BackupStore {

    @Override
    public List<String> getBackupNames() {
        List<Backup> backups = getBackups();
        List<String> backupNames = new ArrayList<>(backups.size());
        for (Backup backup : backups) {
            backupNames.add(backup.getName());
        }
        return backupNames;
    }

    @Override
    public List<Backup> getBackups(long traceId) {
        List<Backup> backups = getBackups();
        Iterator<Backup> it = backups.iterator();
        while (it.hasNext()) {
            Backup backup = it.next();
            if (!(traceId == backup.getTraceId())) {
                it.remove();
            }
        }
        return backups;
    }

    @Override
    public List<Backup> getBackups(long endMs, long lookback) {
        return null;
    }

    @Override
    public Backup getBackup(long id) {
        return null;
    }
}
