package net.pluto.script;

import net.pluto.backup.BackupEnvironment;
import net.pluto.backup.BackupType;
import net.pluto.util.Configuration;
import net.pluto.util.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

public class XtrabackupScriptFileBuilder {

    public static final String BASH_HEAD = "#!/bin/sh";

    public static final String COMMAND_XTRABACKUP = "innobackupex";

    public static final String STANDARD_OUTPUT = "2>&1";

    public static final String COMMAND_CAT = "cat";

    public static final String COMMAND_ERROR_EXIT = "exit 1";

    public static final String COMMAND_FORCE_REMOVE = "rm -rf";

    public static final String COMMAND_REPLACE_CORY = "cp -rf";

    public static final String COMMAND_CHANGE_OWNER = "chown -R";

    public static final String COMMAND_MAKE_DIR = "mkdir -p";

    public static final String COMMAND_MOVE = "mv";

    public static final String _REDIRECT = ">";

    public static final String FUNC_PRINT_AND_CLEAR = "print_and_clear";

    public static final String FUNC_CHECK_RESULT = "check_result";

    public static final String FUNC_SHUTDOWN_MYSQL = "shutdown_mysql";

    public static final String FUNC_STARTUP_MYSQL = "startup_mysql";

    public static final String FUNC_MOVE_DATA_DIR = "move_data_dir";

    public static final String FUNC_MAKE_DATA_DIR = "make_data_dir";

    public synchronized File buildBackupScriptFile(BackupType backupType, ScriptParameter parameter) {
        ScriptStringBuilder fileBuilder = new ScriptStringBuilder();
        fileBuilder.appendWithLineFeed(BASH_HEAD);
        String tmpLog = newTmpLog();
        fileBuilder.appendWithLineFeed(createFunc_print_and_clear(tmpLog));
        fileBuilder.appendWithLineFeed(createFunc_check_result(tmpLog));
        fileBuilder.appendWithLineFeed(createXtrabackupCommand(parameter, tmpLog));
        fileBuilder.appendWithLineFeed(FUNC_CHECK_RESULT);
        fileBuilder.appendWithLineFeed(FUNC_PRINT_AND_CLEAR);
        String scriptDir = Configuration.getInstance().getProperty(Constants.EXECUTABLE_SCRIPT_DIR_KEY);
        String backupBashFile = scriptDir + "/" + backupType.type().toLowerCase() + "_backup.sh";
        return prepareFile(backupBashFile, fileBuilder);
    }

    public synchronized File buildFullRestoreScriptFile(ScriptParameter parameter) {
        ScriptStringBuilder fileBuilder = new ScriptStringBuilder();
        fileBuilder.appendWithLineFeed(BASH_HEAD);
        String tmpLog = newTmpLog();
        fileBuilder.appendWithLineFeed(createFunc_print_and_clear(tmpLog));
        fileBuilder.appendWithLineFeed(createFunc_check_result(tmpLog));
        fileBuilder.appendWithLineFeed(createFunc_shutdown_mysql());
        fileBuilder.appendWithLineFeed(createFunc_move_data_dir());
        fileBuilder.appendWithLineFeed(createFunc_make_data_dir());
        fileBuilder.appendWithLineFeed(createFunc_startup_mysql());
        fileBuilder.appendWithLineFeed(FUNC_SHUTDOWN_MYSQL);
        fileBuilder.appendWithLineFeed(FUNC_MOVE_DATA_DIR);
        fileBuilder.appendWithLineFeed(FUNC_MAKE_DATA_DIR);
        fileBuilder.appendWithLineFeed(createApplyLogCommand(parameter, tmpLog));
        fileBuilder.appendWithLineFeed(FUNC_CHECK_RESULT);
        fileBuilder.appendWithLineFeed(FUNC_PRINT_AND_CLEAR);
        fileBuilder.appendWithLineFeed(createCopyBackCommand(parameter, tmpLog));
        fileBuilder.appendWithLineFeed(FUNC_CHECK_RESULT);
        fileBuilder.appendWithLineFeed(FUNC_PRINT_AND_CLEAR);
        fileBuilder.appendWithLineFeed(FUNC_STARTUP_MYSQL);
        String scriptDir = Configuration.getInstance().getProperty(Constants.EXECUTABLE_SCRIPT_DIR_KEY);
        String restoreBashFile = scriptDir + "/" + "full_restore.sh";
        return prepareFile(restoreBashFile, fileBuilder);
    }

    public synchronized File buildPartialRestoreScriptFile(ScriptParameter parameter) {
        ScriptStringBuilder fileBuilder = new ScriptStringBuilder();
        fileBuilder.appendWithLineFeed(BASH_HEAD);
        String tmpLog = newTmpLog();
        fileBuilder.appendWithLineFeed(createFunc_print_and_clear(tmpLog));
        fileBuilder.appendWithLineFeed(createFunc_check_result(tmpLog));
        fileBuilder.appendWithLineFeed(createFunc_shutdown_mysql());
        fileBuilder.appendWithLineFeed(createFunc_startup_mysql());
        fileBuilder.appendWithLineFeed(FUNC_SHUTDOWN_MYSQL);
        fileBuilder.appendWithLineFeed(createApplyLogCommand(parameter, tmpLog));
        fileBuilder.appendWithLineFeed(FUNC_CHECK_RESULT);
        fileBuilder.appendWithLineFeed(FUNC_PRINT_AND_CLEAR);
        // cp -rf
        fileBuilder.appendWithLineFeed(replaceOriginalFile(parameter));
        fileBuilder.appendWithLineFeed(FUNC_STARTUP_MYSQL);
        String scriptDir = Configuration.getInstance().getProperty(Constants.EXECUTABLE_SCRIPT_DIR_KEY);
        String restoreBashFile = scriptDir + "/" + "partial_restore.sh";
        return prepareFile(restoreBashFile, fileBuilder);
    }


    private File prepareFile(String path, ScriptStringBuilder fileBuilder) {
        File file = new File(path);
        try {
            FileUtils.writeByteArrayToFile(file, fileBuilder.toString().getBytes("UTF-8"));
            file.setExecutable(true, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private String newTmpLog() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return tmpDir + "/" + String.valueOf(System.currentTimeMillis()) + ".tmp";
    }

    private String createFunc_print_and_clear(String tmpLog) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed("print_and_clear()");
        builder.appendWithLineFeed("{");
        builder.appendWithWhitespace(COMMAND_CAT).appendWithLineFeed(tmpLog);
        builder.appendWithWhitespace(COMMAND_FORCE_REMOVE).appendWithLineFeed(tmpLog);
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFunc_check_result(String tmpLog) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed("check_result()");
        builder.appendWithLineFeed("{");
        builder.appendWithLineFeed("if [ -z \"`tail -1 " + tmpLog + "| grep 'completed OK!'`\" ] ; then");
        builder.appendWithLineFeed("print_and_clear").appendWithLineFeed(COMMAND_ERROR_EXIT);
        builder.appendWithLineFeed("fi");
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFunc_shutdown_mysql() {
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment();
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed(FUNC_SHUTDOWN_MYSQL + "()");
        builder.appendWithLineFeed("{");
        builder.appendWithLineFeed("if [ `netstat -lnt | grep " + backupEnvironment.getPort() + "|wc -l` = 1 ]; then");
        builder.appendWithLineFeed(backupEnvironment.getShutdownCommand());
        builder.appendWithLineFeed("fi");
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFunc_move_data_dir() {
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment();
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed(FUNC_MOVE_DATA_DIR + "()");
        builder.appendWithLineFeed("{");
        builder.appendWithLineFeed("if [ -d " + backupEnvironment.getDataBakDir() + " ]; then");
        builder.appendWithWhitespace(COMMAND_FORCE_REMOVE).appendWithLineFeed(backupEnvironment.getDataBakDir());
        builder.appendWithLineFeed("fi");

        builder.appendWithLineFeed("if [ -d " + backupEnvironment.getDataDir() + " ]; then");
        builder.appendWithWhitespace(COMMAND_MOVE)
                .appendWithWhitespace(backupEnvironment.getDataDir())
                .appendWithLineFeed(backupEnvironment.getDataBakDir());
        builder.appendWithLineFeed("fi");
        builder.appendWithLineFeed("}");
        return builder.toString();
    }


    private String createFunc_make_data_dir() {
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment();
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed(FUNC_MAKE_DATA_DIR + "()");
        builder.appendWithLineFeed("{");
        builder.appendWithWhitespace(COMMAND_MAKE_DIR).appendWithLineFeed(backupEnvironment.getDataDir());
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFunc_startup_mysql() {
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment();
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed(FUNC_STARTUP_MYSQL + "()");
        builder.appendWithLineFeed("{");
        builder.appendWithWhitespace(COMMAND_CHANGE_OWNER)
                .appendWithWhitespace(backupEnvironment.getMysqlGroup() + ":" + backupEnvironment.getMysqlUser())
                .appendWithLineFeed(backupEnvironment.getDataDir());
        builder.appendWithLineFeed(backupEnvironment.getStartupCommand());
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String replaceOriginalFile(ScriptParameter parameter) {
        BackupEnvironment backupEnvironment = Configuration.getInstance().getBackupEnvironment();
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithWhitespace(COMMAND_REPLACE_CORY).
                appendWithWhitespace(parameter.getPair(ScriptParameter.PARAM_BASE_DIR).value() + "/.")
                .appendWithLineFeed(backupEnvironment.getDataDir() + "/");
        return builder.toString();
    }

    private String createApplyLogCommand(ScriptParameter parameters, String tmpLog) {
        String cmd = createXtrabackupCommand(parameters, tmpLog);
        return cmd.replace(ScriptParameter.PARAM_COPY_BACK, StringUtils.EMPTY);
    }

    private String createCopyBackCommand(ScriptParameter parameters, String tmpLog) {
        String cmd = createXtrabackupCommand(parameters, tmpLog);
        return cmd.replace(ScriptParameter.PARAM_APPLY_LOG, StringUtils.EMPTY);
    }

    private String createXtrabackupCommand(ScriptParameter parameters, String tmpLog) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithWhitespace(COMMAND_XTRABACKUP);
        for (ScriptParameter.Pair paramPair : parameters.values()) {
            builder.appendWithWhitespace(paramPair.toString());
        }
        builder.appendWithWhitespace(_REDIRECT).appendWithWhitespace(tmpLog).appendWithWhitespace(STANDARD_OUTPUT);
        return builder.toString();
    }
}
