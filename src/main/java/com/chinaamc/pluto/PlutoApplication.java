package com.chinaamc.pluto;

import com.chinaamc.pluto.backup.BackupEnvironment;
import com.chinaamc.pluto.exceptions.ConfigurationException;
import com.chinaamc.pluto.exceptions.InitializationException;
import com.chinaamc.pluto.util.BackupEnvironmentCodec;
import com.chinaamc.pluto.util.Codec;
import com.chinaamc.pluto.util.Configuration;
import com.chinaamc.pluto.util.Constants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;

import java.io.File;
import java.util.List;

public class PlutoApplication {

    public static void start(String[] args) {
        SpringApplication.run(Pluto.class, args);
    }

    private static void initConfiguration() throws Exception {
        // JVM参数
        resolvePropertyFromSystem();
        // backup-environment.json
        resolveBackupEnvironment();
        // backup restore script
        resolveBackupScripts();
    }


    private static void resolvePropertyFromSystem() throws Exception {
        String confLocation = System.getProperty(Constants.PLUTO_CONF_FILE_PROP_KEY);
        if (StringUtils.isEmpty(confLocation)) {
            throw new InitializationException("pluto.conf.file property not set");
        }
        Configuration.getInstance().init(new File(confLocation));

        String baseDir = System.getProperty(Constants.PLUTO_BASE_DIR_KEY);
        if (StringUtils.isEmpty(baseDir)) {
            throw new InitializationException("pluto.base.dir property not set");
        }

        if (!baseDir.endsWith("/")) {
            baseDir = baseDir + "/";
        }
        Configuration.getInstance().setProperty(Constants.PLUTO_BASE_DIR_KEY, baseDir);
    }

    private static final BackupEnvironmentCodec CODEC = new BackupEnvironmentCodec(Codec.JSON);

    private static void resolveBackupEnvironment() throws Exception {
        String filePath = Configuration.getInstance().getProperty(Constants.PLUTO_CONF_DIR_KEY)
                + "/backup-environment.json";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ConfigurationException("backup-environment.json does not exist");
        }
        List<BackupEnvironment> backupEnvironments = CODEC.readBackupEnvironments(FileUtils.readFileToByteArray(file));
        if (CollectionUtils.isEmpty(backupEnvironments)) {
            throw new ConfigurationException("backup-environment.json is empty");
        }
        Configuration.getInstance().addBackupEnvironments(backupEnvironments);
    }

    private static void resolveBackupScripts() throws Exception {

    }
}