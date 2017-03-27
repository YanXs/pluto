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
     * 获取管理员文件路径
     *
     * @return
     */
    public static String getBackupUserFilePath() {
        return CONFIGURATION.getProperty(Constants.PLUTO_CONF_DIR_KEY) + "/pluto-users.json";
    }

}
