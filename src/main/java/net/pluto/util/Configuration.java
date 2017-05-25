package net.pluto.util;

import net.pluto.backup.BackupEnvironment;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.File;
import java.util.Collection;

public class Configuration {

    private static final PropertiesConfiguration CONFIG = new PropertiesConfiguration();

    private static final Configuration CONFIGURATION = new Configuration();

    private final BackupEnvironmentNavigator navigator = new BackupEnvironmentNavigator();

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

    public void addEnvironment(BackupEnvironment environment) {
        navigator.add(environment);
    }

    public BackupEnvironment getBackupEnvironment(String instance) {
        BackupEnvironment environment = navigator.get(instance);
        if (environment == null) {
            throw new IllegalStateException("wrong argument instance: { " + instance + " }" +
                    "please check backup-environment.json");
        }
        return environment;
    }

    public Collection<BackupEnvironment> backupEnvironments() {
        return navigator.backupEnvironments();
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
