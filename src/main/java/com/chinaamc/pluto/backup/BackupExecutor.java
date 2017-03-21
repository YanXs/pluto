package com.chinaamc.pluto.backup;

import com.chinaamc.pluto.backup.store.BackupStore;
import com.chinaamc.pluto.script.ScriptBuilder;
import com.chinaamc.pluto.script.ScriptExecutor;
import com.chinaamc.pluto.util.BackupUtil;
import com.chinaamc.pluto.util.Configuration;
import com.chinaamc.pluto.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class BackupExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final BackupStore backupStore;

    private final ScriptExecutor scriptExecutor;

    public BackupExecutor(BackupStore backupStore) {
        this.backupStore = backupStore;
        this.scriptExecutor = new ScriptExecutor();
    }

    public boolean executeBackup(Backup backup) {
        if (CollectionUtils.isEmpty(backupStore.getBackups()) && !BackupUtil.isFullBackup(backup)) {
            throw new IllegalStateException("first backup must be full backup");
        }
        return doExecuteBackup(backup);
    }

    private boolean doExecuteBackup(Backup backup) {
        String backupDir = Configuration.getBackupDataDirPath();
        ScriptBuilder scriptBuilder;
        if (backup.getBackupType().equals(Constants.BACKUP_TYPE_FULL)) {
            scriptBuilder = new ScriptBuilder(Configuration.getFullBackupBashFilePath());
            scriptBuilder.appendArg(backupDir);
        } else {
            scriptBuilder = new ScriptBuilder(Configuration.getIncrementalBackupBashFilePath());
            scriptBuilder.appendArg(backupDir);
            List<Backup> backups = backupStore.getBackups();
            if (CollectionUtils.isEmpty(backups)) {
                throw new IllegalStateException("incremental backup should have base backup");
            }
            Backup lastBack = backups.get(backups.size() - 1);
            scriptBuilder.appendArg(lastBack.getBackupDirectory());
        }
        // append backup name to the end of backup log file
        scriptBuilder.appendArg(backup.getName());
        long start = System.currentTimeMillis();
        if (scriptExecutor.execute(scriptBuilder) == 0) {
            try {
                Backup.Builder builder = backup.newBuilder();
                builder.duration(getDuration(start));
                backupStore.append(builder.build());
            } catch (Exception e) {
                LOGGER.error("append backup failed", e);
                return false;
            }
        }
        return true;
    }

    public List<Backup> getBackups() {
        return backupStore.getBackups();
    }

    private Long getDuration(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    public boolean executeRollback(String id) {
        List<Backup> backups = backupStore.getBackups();
        if (backups == null) {
            throw new IllegalStateException("there is no backups");
        }
        Backup backup = backupStore.getBackup(Long.parseLong(id));
        if (backup == null) {
            throw new IllegalStateException("id dos not exist");
        }
        String backupDir = Configuration.getBackupDataDirPath();
        ScriptBuilder scriptBuilder;
        if (backup.getBackupType().equals(Constants.BACKUP_TYPE_FULL)) {
            scriptBuilder = new ScriptBuilder(Configuration.getFullRestoreBashFilePath());
            scriptBuilder.appendArg(backupDir);
            scriptBuilder.appendArg(Configuration.getMysqlInstancePort());
            scriptBuilder.appendArg(Configuration.getMysqlDataDir());
            scriptBuilder.appendArg(Configuration.getMysqlDataBakDir());
            scriptBuilder.appendArg(backup.getBackupDirectory());
        } else {
            String fullBackupDirectory = null;
            for (Backup backupFilter : backups) {
                if (Objects.equals(backup.getTraceId(), backupFilter.getTraceId()) && backupFilter.getBackupType() == 0) {
                    fullBackupDirectory = backupFilter.getBackupDirectory();
                    break;
                }
            }
            if (fullBackupDirectory == null) {
                throw new IllegalStateException("Incremental restore must have a fullBackupDirectory");
            }
            scriptBuilder = new ScriptBuilder(Configuration.getIncrementalRestoreBashFilePath());
            scriptBuilder.appendArg(backupDir);
            scriptBuilder.appendArg(Configuration.getMysqlInstancePort());
            scriptBuilder.appendArg(Configuration.getMysqlDataDir());
            scriptBuilder.appendArg(Configuration.getMysqlDataBakDir());
            scriptBuilder.appendArg(backup.getBackupDirectory());
            scriptBuilder.appendArg(fullBackupDirectory);
        }
        int result = scriptExecutor.execute(scriptBuilder);

        return result == 0;
    }

    public void removeBackup(Long id) {
        Backup backup = backupStore.getBackup(id);
        backupStore.removeBackup(backup);
    }
}
