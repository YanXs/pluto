package com.chinaamc.pluto.test;

import com.chinaamc.pluto.backup.BackupType;
import com.chinaamc.pluto.script.ScriptParameter;
import com.chinaamc.pluto.script.ScriptStringBuilder;
import com.chinaamc.pluto.script.XtrabackupScriptFileBuilder;
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
        pair.key(ScriptParameter.PARAM_BACKUP_DIR).keyVisible(false).value("C:/ta");
        scriptParameter.addPair(pair);
        builder.buildBackupScriptFile(BackupType.Partial, scriptParameter);
    }
}
