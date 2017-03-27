package com.chinaamc.pluto.test;

import com.chinaamc.pluto.backup.BackupEnvironment;
import com.chinaamc.pluto.backup.BackupType;
import com.chinaamc.pluto.script.ScriptParameter;
import com.chinaamc.pluto.script.ScriptStringBuilder;
import com.chinaamc.pluto.script.XtrabackupScriptFileBuilder;
import com.chinaamc.pluto.util.Configuration;
import com.chinaamc.pluto.util.Constants;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Arrays;
import java.util.List;

/**
 * @author Xs.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptFileBuilderTest {

    @BeforeClass
    public static void before(){
        String baseDir = System.getProperty(Constants.PLUTO_BASE_DIR_KEY);
        if (!baseDir.endsWith("/")) {
            baseDir = baseDir + "/";
        }
        Configuration.getInstance().setProperty(Constants.EXECUTABLE_SCRIPT_DIR_KEY, baseDir + "script");
    }

    @Test
    public void test_build_full_backup_file() {
        XtrabackupScriptFileBuilder builder = new XtrabackupScriptFileBuilder();
        ScriptParameter scriptParameter = new ScriptParameter();
        ScriptParameter.Pair pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_BACKUP_DIR).keyVisible(false).value("/ta");
        scriptParameter.addPair(pair);
        builder.buildBackupScriptFile(BackupType.Full, scriptParameter);
    }

    @Test
    public void test_build_incremental_backup_file() {
        XtrabackupScriptFileBuilder builder = new XtrabackupScriptFileBuilder();
        ScriptParameter scriptParameter = new ScriptParameter();
        ScriptParameter.Pair pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_INCREMENTAL).valueVisible(false);
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_BACKUP_DIR).keyVisible(false).value("/ta");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_INCREMENTAL_BASE).value("/ta/a");
        scriptParameter.addPair(pair);
        builder.buildBackupScriptFile(BackupType.Incremental, scriptParameter);
    }

    @Test
    public void test_build_partial_backup_file() {
        XtrabackupScriptFileBuilder builder = new XtrabackupScriptFileBuilder();
        ScriptParameter scriptParameter = new ScriptParameter();
        ScriptParameter.Pair pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value("root");
        scriptParameter.addPair(pair);

        List<String> databases = Arrays.asList("account", "mutual");
        ScriptStringBuilder b = new ScriptStringBuilder();
        b.append("\"");
        for (String database : databases) {
            b.appendWithWhitespace(database);
        }
        b.append("\"");
        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_DATABASE).value(b.toString());
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_BACKUP_DIR).keyVisible(false).value("/ta");
        scriptParameter.addPair(pair);
        builder.buildBackupScriptFile(BackupType.Partial, scriptParameter);
    }


    @Test
    public void test_build_full_restore_file() {
        Configuration configuration = Configuration.getInstance();
        BackupEnvironment.Builder builder = new BackupEnvironment.Builder();
        builder.mysqlGroup("mysql")
                .mysqlUser("mysql")
                .dataDir("/data/tadata")
                .dataBakDir("/data/tadata_bak")
                .port(3306)
                .startupCommand("/etc/init.d/mysqld start")
                .shutdownCommand("/etc/init.d/mysqld start");
        configuration.setBackupEnvironment(builder.build());
        XtrabackupScriptFileBuilder xbuilder = new XtrabackupScriptFileBuilder();
        ScriptParameter scriptParameter = new ScriptParameter();
        ScriptParameter.Pair pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value("root");
        scriptParameter.addPair(pair);

        pair =scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_APPLY_LOG).valueVisible(false);
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_COPY_BACK).valueVisible(false);
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_BASE_DIR).keyVisible(false).value("/backup/2017-10-03_11-22-33");
        scriptParameter.addPair(pair);
        xbuilder.buildFullRestoreScriptFile(scriptParameter);
    }

    @Test
    public void test_build_partial_restore_file() {
        Configuration configuration = Configuration.getInstance();
        BackupEnvironment.Builder builder = new BackupEnvironment.Builder();
        builder.mysqlGroup("mysql")
                .mysqlUser("mysql")
                .dataDir("/data/tadata")
                .dataBakDir("/data/tadata_bak")
                .port(3306)
                .startupCommand("/etc/init.d/mysqld start")
                .shutdownCommand("/etc/init.d/mysqld start");
        configuration.setBackupEnvironment(builder.build());
        XtrabackupScriptFileBuilder xbuilder = new XtrabackupScriptFileBuilder();
        ScriptParameter scriptParameter = new ScriptParameter();
        ScriptParameter.Pair pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_USER).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_PASSWORD).value("root");
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_APPLY_LOG).valueVisible(false);
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_EXPORT).valueVisible(false);
        scriptParameter.addPair(pair);

        pair = scriptParameter.newPair();
        pair.key(ScriptParameter.PARAM_BASE_DIR).keyVisible(false).value("/backup/2017-10-03_11-22-33");
        scriptParameter.addPair(pair);
        xbuilder.buildPartialRestoreScriptFile(scriptParameter);
    }
}
