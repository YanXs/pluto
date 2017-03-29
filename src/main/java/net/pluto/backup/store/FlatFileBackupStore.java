package net.pluto.backup.store;

import net.pluto.backup.Backup;
import net.pluto.backup.BackupCodec;
import net.pluto.backup.BackupEnvironment;
import net.pluto.util.Configuration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class FlatFileBackupStore extends AbstractBackupStore {

    private final BackupCodec backupCodec;

    public FlatFileBackupStore(BackupCodec backupCodec) {
        this.backupCodec = backupCodec;
    }

    @Override
    public long append(Backup backup) {
        return backup.getTraceId();
    }

    @Override
    public synchronized void save(List<Backup> backups) {
        byte[] bytes = backupCodec.writeBackups(backups);
        BackupEnvironment environment = Configuration.getInstance().getBackupEnvironment();
        File backupLogFile = new File(environment.getBackupLog());
        if (backupLogFile.exists()) {
            try {
                FileUtils.copyFile(backupLogFile, new File(environment.getBackupLogBak()));
                FileUtils.writeByteArrayToFile(backupLogFile, bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            try {
                if (backupLogFile.createNewFile()) {
                    FileUtils.writeByteArrayToFile(backupLogFile, bytes);
                } else {
                    throw new IOException("cannot create " + backupLogFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized List<Backup> getBackups() {
        BackupEnvironment environment = Configuration.getInstance().getBackupEnvironment();
        File file = new File(environment.getBackupLog());
        if (file.exists()) {
            try {
                byte[] bytes = FileUtils.readFileToByteArray(file);
                List<Backup> backups = backupCodec.readBackups(bytes);
                if (CollectionUtils.isEmpty(backups)) {
                    return null;
                } else {
                    return backups;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void removeBackup(Backup backup) {
        if (backup == null) {
            throw new IllegalArgumentException("backup must not be null");
        }
        try {
            FileUtils.forceDelete(new File(backup.getBackupDirectory()));
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
