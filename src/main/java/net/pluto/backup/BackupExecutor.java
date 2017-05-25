package net.pluto.backup;

import net.pluto.backup.store.BackupStore;
import net.pluto.script.*;
import net.pluto.util.Configuration;
import net.pluto.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

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
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment(backup.getInstance());
        addCommonParameter(parameter, backupEnvironment);
        if (backup.getBackupType() == BackupType.Full) {
            addBackupDirParameter(parameter);
        } else if (backup.getBackupType() == BackupType.Incremental) {
            ScriptParameter.Pair pair = parameter.newPair();
            pair.key(ScriptParameter.PARAM_INCREMENTAL).valueVisible(false);
            parameter.addPair(pair);

            addBackupDirParameter(parameter);
            List<Backup> backups = backupStore.getBackups();
            if (CollectionUtils.isEmpty(backups)) {
                throw new IllegalStateException("incremental backup should have base backup");
            }
            Backup lastBack = backups.get(backups.size() - 1);
            pair = parameter.newPair();
            pair.key(ScriptParameter.PARAM_INCREMENTAL_BASE).value(lastBack.getBackupDirectory());
            parameter.addPair(pair);
        } else {
            if (CollectionUtils.isEmpty(databases)) {
                throw new IllegalArgumentException("databases should not be null in partial backup");
            }
            if (databases.size() == 1 && databases.get(0).equals("mysql")) {
                throw new IllegalArgumentException("database to backup should contain other target except mysql");
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
            parameter.addPair(pair);
            addBackupDirParameter(parameter);
        }

        File sh = scriptFileBuilder.buildBackupScriptFile(backup.getBackupType(), parameter);
        scriptBuilder = new ScriptBuilder(sh.getPath());

        long start = System.currentTimeMillis();
        if (scriptExecutor.execute(scriptBuilder) == 0) {
            try {
                Backup.Builder builder = backup.newBuilder();
                builder.duration(getDuration(start));
                if (databases != null) {
                    builder.databases(databases);
                }
                backupStore.append(builder.build());
            } catch (Exception e) {
                LOGGER.error("append backup failed", e);
                return false;
            }
        }
        return true;
    }

    private void addCommonParameter(ScriptParameter parameter, BackupEnvironment environment) {
        ScriptParameter.Pair pair = parameter.newPair();
        pair.key(ScriptParameter.DEFAULTS_FILE).value(environment.getDefaultsFile());
        parameter.addPair(pair);
        pair = parameter.newPair();

        pair.key(ScriptParameter.PARAM_USER).value(environment.getUsername());
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value(environment.getPassword());
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_MEMORY).value(environment.getXtrabackupMemory());
        parameter.addPair(pair);

        pair = parameter.newPair();
        pair.key(ScriptParameter.PARAM_PARALLEL).value(environment.getXtrabackupParallel());
        parameter.addPair(pair);
    }

    private void addBackupDirParameter(ScriptParameter parameter) {
        ScriptParameter.Pair pair = parameter.newPair();
        pair
                .key(ScriptParameter.PARAM_BACKUP_DIR)
                .keyVisible(false)
                .value(Configuration.getInstance().getProperty(Constants.BACKUP_DIR_KEY));
        parameter.addPair(pair);
    }

    public List<Backup> getBackups() {
        return backupStore.getBackups();
    }

    public List<String> getBackupNames() {
        return backupStore.getBackupNames();
    }

    private Long getDuration(long start) {
        return (System.currentTimeMillis() - start) / 1000;
    }

    public boolean executeRestore(String id) {
        List<Backup> backups = backupStore.getBackups();
        if (backups == null) {
            throw new IllegalStateException("there is no backups");
        }
        Backup backup = backupStore.getBackup(Long.parseLong(id));
        if (backup == null) {
            throw new IllegalStateException("id dos not exist");
        }
        ScriptParameter scriptParameter = new ScriptParameter();
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment(backup.getInstance());
        // add common parameter
        addCommonParameter(scriptParameter, backupEnvironment);
        ScriptBuilder scriptBuilder;
        if (backup.getBackupType() == BackupType.Full) {
            ScriptParameter.Pair pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_APPLY_LOG).valueVisible(false);
            scriptParameter.addPair(pair);

            pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_COPY_BACK).valueVisible(false);
            scriptParameter.addPair(pair);

            pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_BASE_DIR).keyVisible(false).value(backup.getBackupDirectory());
            scriptParameter.addPair(pair);

            File restoreScript = scriptFileBuilder.buildFullRestoreScriptFile(scriptParameter, backupEnvironment);
            scriptBuilder = new ScriptBuilder(restoreScript.getPath());
        } else if (backup.getBackupType() == BackupType.Partial) {
            ScriptParameter.Pair pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_APPLY_LOG).valueVisible(false);
            scriptParameter.addPair(pair);

            pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_EXPORT).valueVisible(false);
            scriptParameter.addPair(pair);

            pair = scriptParameter.newPair();
            pair.key(ScriptParameter.PARAM_BASE_DIR).keyVisible(false).value(backup.getBackupDirectory());
            scriptParameter.addPair(pair);
            File restoreScript = scriptFileBuilder.buildPartialRestoreScriptFile(scriptParameter, backupEnvironment);
            scriptBuilder = new ScriptBuilder(restoreScript.getPath());
        } else {
            throw new UnsupportedOperationException("incremental restore unsupported");
        }
        return scriptExecutor.execute(scriptBuilder) == 0;
    }

    public void removeBackup(Long id) {
        Backup backup = backupStore.getBackup(id);
        backupStore.removeBackup(backup);
    }
}
