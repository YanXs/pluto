package com.chinaamc.pluto.script;

import com.chinaamc.pluto.backup.BackupType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class XtrabackupScriptFileBuilder {

    public static final String BASH_HEAD = "#!/bin/sh";

    public static final String COMMAND_XTRABACKUP = "innobackupex";

    public static final String STANDARD_OUTPUT = "2>&1";

    public static final String COMMAND_ECHO = "echo";

    public static final String COMMAND_CAT = "cat";

    public static final String COMMAND_ERROR_EXIT = "exit 1";

    public static final String COMMAND_FORCE_REMOVE = "rm -rf";

    public static final String COMMAND_REPLACE_CORY = "cp -rf";

    public static final String _REDIRECT = ">";

    public static final String _APPEND = ">>";

    public static final String FUNC_PRINT_AND_CLEAR = "print_and_clear";

    public static final String FUNC_CHECK_RESULT = "check_result";

    public synchronized File buildBackupScriptFile(BackupType backupType, ScriptParameter parameter) {
        ScriptStringBuilder fileBuilder = new ScriptStringBuilder();
        fileBuilder.appendWithLineFeed(BASH_HEAD);
        String tmpLog = newTmpLog();
        fileBuilder.appendWithLineFeed(createFun_print_and_clear(tmpLog));
        fileBuilder.appendWithLineFeed(createFun_check_result(tmpLog));
        fileBuilder.appendWithLineFeed(createXtrabackupCommand(parameter, tmpLog));
        fileBuilder.appendWithLineFeed(FUNC_CHECK_RESULT);
        fileBuilder.appendWithLineFeed(FUNC_PRINT_AND_CLEAR);
        String tmpDir = System.getProperty("java.io.tmpdir");
        String backupBashFile = tmpDir + "/" + backupType.type().toLowerCase() + "_backup.sh";
        File file = new File(backupBashFile);
        try {
            FileUtils.writeByteArrayToFile(file, fileBuilder.toString().getBytes("UTF-8"));
            file.setExecutable(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private String newTmpLog() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return tmpDir + "/" + String.valueOf(System.currentTimeMillis()) + ".tmp";
    }

    private String createFun_print_and_clear(String tmpLogFile) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed("print_and_clear()");
        builder.appendWithLineFeed("{");
        builder.appendWithWhitespace(COMMAND_CAT).appendWithLineFeed(tmpLogFile);
        builder.appendWithWhitespace(COMMAND_FORCE_REMOVE).appendWithLineFeed(tmpLogFile);
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFun_check_result(String tmpLog) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithLineFeed("check_result()");
        builder.appendWithLineFeed("{");
        builder.appendWithLineFeed("if [ -z \"`tail -1 " + tmpLog + "| grep 'completed OK!'`\" ] ; then");
        builder.appendWithLineFeed("print_and_clear").appendWithLineFeed(COMMAND_ERROR_EXIT);
        builder.appendWithLineFeed("fi");
        builder.appendWithLineFeed("}");
        return builder.toString();
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
