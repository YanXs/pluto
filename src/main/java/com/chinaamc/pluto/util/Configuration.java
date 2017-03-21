package com.chinaamc.pluto.util;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class Configuration {

    private static final PropertiesConfiguration CONFIG = new PropertiesConfiguration();

    private static final Configuration CONFIGURATION = new Configuration();

    private Configuration() {
    }

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

    public static String getBackupLogFilePath() {
        String dataDir = CONFIGURATION.getProperty(Constants.BACKUP_LOG_DIR);
        return dataDir + "/backup.data";
    }

    public static String getBackupLogBakFilePath() {
        String dataDir = CONFIGURATION.getProperty(Constants.BACKUP_LOG_DIR);
        return dataDir + "/backup.data.bak";
    }

    public static String getBackupDataDirPath() {
        String backupDir = CONFIGURATION.getProperty(Constants.BACKUP_BASE_DIR_KEY);
        if (StringUtils.isEmpty(backupDir)) {
            throw new com.chinaamc.pluto.exceptions.ConfigurationException("backup dir is illegal");
        }
        return backupDir;
    }

    public static String getFullBackupBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/fullBackup.sh";
    }

    public static String getIncrementalBackupBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/incrBackup.sh";
    }

    public static String getFullRestoreBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/fullRestore.sh";
    }

    public static String getIncrementalRestoreBashFilePath() {
        String scriptExecutableDir = CONFIGURATION.getProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY);
        return scriptExecutableDir + "/incrRestore.sh";
    }

    public static String getXtrabackupLogInfoFilePath(File baseDirectory) {
        assert baseDirectory != null;
        return baseDirectory.getAbsolutePath() + "/xtrabackup_log_info";
    }

    public static String getMysqlDataDir() {
        return CONFIGURATION.getProperty(Constants.MYSQL_DATA_DIR_KEY);
    }

    public static String getMysqlDataBakDir() {
        return CONFIGURATION.getProperty(Constants.MYSQL_DATA_BAK_DIR_KEY);
    }

    public static String getMysqlInstancePort() {
        return CONFIGURATION.getProperty(Constants.MYSQL_PORT_KEY);
    }

}
