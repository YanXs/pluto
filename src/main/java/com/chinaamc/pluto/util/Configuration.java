package com.chinaamc.pluto.util;

import com.chinaamc.pluto.backup.BackupEnvironment;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class Configuration {

    private static final PropertiesConfiguration CONFIG = new PropertiesConfiguration();

    private static final Configuration CONFIGURATION = new Configuration();

    private BackupEnvironment backupEnvironment;

    public static Configuration getInstance() {
        return CONFIGURATION;
    }

    public void init(File file) throws ConfigurationException {
        CONFIG.load(file);
    }

    public String getProperty(String key) {
        return CONFIG.getString(key);
    }

    public void setProperty(String key, String value) {
        CONFIG.setProperty(key, value);
    }

    public BackupEnvironment getBackupEnvironment() {
        return backupEnvironment;
    }

    public void setBackupEnvironment(BackupEnvironment backupEnvironment) {
        this.backupEnvironment = backupEnvironment;
    }

    /**
     * 获取备份日志路径
     *
     * @return
     */
    public static String getBackupLogFilePath() {
        String backupLogDir = CONFIGURATION.getProperty(Constants.BACKUP_LOG_DIR);
        return backupLogDir + "/backup.data";
    }

    /**
     * 获取备份日志备份文件路径
     *
     * @return
     */
    public static String getBackupLogBakFilePath() {
        String dataDir = CONFIGURATION.getProperty(Constants.BACKUP_LOG_DIR);
        return dataDir + "/backup.data.bak";
    }

    /**
     * 获取备份文件存放路径
     *
     * @return
     */
    public static String getBackupDataDirPath() {
        String backupDir = CONFIGURATION.getProperty(Constants.BACKUP_BASE_DIR_KEY);
        if (StringUtils.isEmpty(backupDir)) {
            throw new com.chinaamc.pluto.exceptions.ConfigurationException("backup dir is illegal");
        }
        return backupDir;
    }

    /**
     * 获取管理员文件路径
     *
     * @return
     */
    public static String getBackupUserFilePath() {
        return CONFIGURATION.getProperty(Constants.PLUTO_CONF_DIR_KEY) + "/pluto-users.json";
    }

    /**
     * 获取全备份恢复脚本路径
     *
     * @return
     */
    public static String getFullRestoreBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/fullRestore.sh";
    }

    /**
     * 获取增量恢复脚本路径
     *
     * @return
     */
    public static String getIncrementalRestoreBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/incrRestore.sh";
    }

    /**
     * 获取xtrabackup_log_info路径（备份后生成）
     *
     * @param baseDirectory
     * @return
     */
    public static String getXtrabackupLogInfoFilePath(File baseDirectory) {
        assert baseDirectory != null;
        return baseDirectory.getAbsolutePath() + "/xtrabackup_log_info";
    }
}
