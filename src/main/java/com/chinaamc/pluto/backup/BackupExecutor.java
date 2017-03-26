package com.chinaamc.pluto.backup;

import com.chinaamc.pluto.backup.store.BackupStore;
import com.chinaamc.pluto.script.*;
import com.chinaamc.pluto.util.Configuration;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class BackupExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final BackupStore backupStore;

    private final ScriptExecutor scriptExecutor;

    private final XtrabackupScriptFileBuilder scriptFileBuilder;

    public BackupExecutor(BackupStore backupStore) {
        this.backupStore = backupStore;
        this.scriptFileBuilder = new XtrabackupScriptFileBuilder();
        this.scriptExecutor = new ScriptExecutor();
    }

    public boolean executeBackup(Backup backup, List<String> databases) {
        if (CollectionUtils.isEmpty(backupStore.getBackups()) && backup.getBackupType() == BackupType.Incremental) {
            throw new IllegalStateException("first backup must not be incremental backup");
        }
        return doExecuteBackup(backup, databases);
    }

    private boolean doExecuteBackup(Backup backup, List<String> databases) {
        ScriptBuilder scriptBuilder;
        ScriptParameter parameter = new ScriptParameter();
        // add common parameter
        addCommonParameter(parameter);
        if (backup.getBackupType() == BackupType.Full) {
            // add backup dir
            addBackupDirParameter(parameter);
        } else if (backup.getBackupType() == BackupType.Incremental) {
            // --incremental
            ScriptParameter.Pair pair = parameter.newPair();
            pair.key(ScriptParameter.PARAM_INCREMENTAL).valueVisible(false);
            parameter.addPair(pair);

            addBackupDirParameter(parameter);
            List<Backup> backups = backupStore.getBackups();
            if (CollectionUtils.isEmpty(backups)) {
                throw new IllegalStateException("incremental backup should have base backup");
            }
            Backup lastBack = backups.get(backups.size() - 1);
            // --incremental-basedir=$BASE_BACKUP
            pair = parameter.newPair();
            pair.key(ScriptParameter.PARAM_INCREMENTAL_BASE).value(lastBack.getBackupDirectory());
        } else {
            if (CollectionUtils.isEmpty(databases)) {
                throw new IllegalArgumentException("databases should not be null in partial backup");
            }
            // add databases
            ScriptStringBuilder builder = new ScriptStringBuilder();
            builder.append("\"");
            for (String database : databases) {
                builder.appendWithWhitespace(database);
            }
            builder.append("\"");
            ScriptParameter.Pair pair = parameter.newPair();
            pair.key(ScriptParameter.PARAM_DATABASE).value(builder.toString());

            addBackupDirParameter(parameter);
        }

        File sh = scriptFileBuilder.buildBackupScriptFile(backup.getBackupType(), parameter);
        scriptBuilder = new ScriptBuilder(sh.getPath());

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

    private void addCommonParameter(ScriptParameter parameter) {
        BackupEnvironment environment = Configuration.getInstance().getBackupEnvironment();
        ScriptParameter.Pair pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value(environment.getUsername());
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value(environment.getPassword());
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_MEMORY).value("4G");
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_PARALLEL).value("8");
        parameter.addPair(pair);
    }

    private void addBackupDirParameter(ScriptParameter parameter) {
        BackupEnvironment environment = Configuration.getInstance().getBackupEnvironment();
        ScriptParameter.Pair pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_BACKUP_DIR).keyVisible(false).value(environment.getBackupDir());
        parameter.addPair(pair);
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
        if (backup.getBackupType() == BackupType.Full) {
            scriptBuilder = new ScriptBuilder(Configuration.getFullRestoreBashFilePath());
            scriptBuilder.appendArg(backupDir);
//            scriptBuilder.appendArg(Configuration.getMysqlInstancePort());
//            scriptBuilder.appendArg(Configuration.getMysqlDataDir());
//            scriptBuilder.appendArg(Configuration.getMysqlDataBakDir());
            scriptBuilder.appendArg(backup.getBackupDirectory());
        } else {
            String fullBackupDirectory = null;
            for (Backup backupFilter : backups) {
                if (Objects.equals(backup.getTraceId(), backupFilter.getTraceId()) && backupFilter.getBackupType() == BackupType.Full) {
                    fullBackupDirectory = backupFilter.getBackupDirectory();
                    break;
                }
            }
            if (fullBackupDirectory == null) {
                throw new IllegalStateException("Incremental restore must have a fullBackupDirectory");
            }
            scriptBuilder = new ScriptBuilder(Configuration.getIncrementalRestoreBashFilePath());
            scriptBuilder.appendArg(backupDir);
//            scriptBuilder.appendArg(Configuration.getMysqlInstancePort());
//            scriptBuilder.appendArg(Configuration.getMysqlDataDir());
//            scriptBuilder.appendArg(Configuration.getMysqlDataBakDir());
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
