package com.chinaamc.pluto;

import com.chinaamc.pluto.exceptions.InitializationException;
import com.chinaamc.pluto.util.Configuration;
import com.chinaamc.pluto.util.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Pluto {

    public static void main(String[] args) throws Exception {
        init(args);
        SpringApplication.run(Pluto.class, args);
    }

    private static void init(String[] args) throws Exception {
        String confLocation = System.getProperty(Constants.PLUTO_CONF_FILE_PROP_KEY);
        if (StringUtils.isEmpty(confLocation)) {
            for (String arg : args) {
                if (arg.startsWith(Constants.PLUTO_CONF_FILE_PROP_KEY)) {
                    confLocation = arg.substring(arg.indexOf("=") + 1);
                    break;
                }
            }
        }
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
        Configuration.getInstance().setProperty(Constants.SCRIPT_EXECUTABLE_DIR_KEY, baseDir + "bin");
        String backupLogDir = Configuration.getInstance().getProperty(Constants.BACKUP_LOG_DIR);
        if (StringUtils.isEmpty(backupLogDir)) {
            Configuration.getInstance().setProperty(Constants.BACKUP_LOG_DIR, baseDir + "data");
        }
    }
}
