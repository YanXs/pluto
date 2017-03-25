package com.chinaamc.pluto.script;

import java.io.File;
import java.util.Map;

public class XtrabackupScriptFileBuilder {

    public static final String BASH_HEAD = "#!/bin/sh";

    public static final String COMMAND_XTRABACKUP = "innobackupex";

    public static final String PARAM_USER = "--user";

    public static final String PARAM_PASSWORD = "--password";

    public static final String PARAM_DATABASE = "--databases";

    public static final String PARAM_MEMORY = "--use-memory";

    public static final String PARAM_PARALLEL = "--parallel";

    public static final String PARAM_APPLY_LOG = "--apply-log";

    public static final String PARAM_COPY_BACK = "--copy-back";

    public static final String PARAM_EXPORT = "--export";

    public static final String STANDARD_OUTPUT = "2>&1";

    public static final String COMMAND_ECHO = "echo";

    public static final String COMMAND_CAT = "cat";

    public static final String COMMAND_EXIT = "exit";

    public static final String COMMAND_FORCE_REMOVE = "rm -rf";

    public static final String COMMAND_REPLACE_CORY = "cp -rf";

    public static final String _REDIRECT = ">";

    public static final String _APPEND = ">>";


    public synchronized File buildFullBackupScriptFile(Map<String, String> parameters) {
        ScriptStringBuilder fileBuilder = new ScriptStringBuilder();
        fileBuilder.appendWithLineFeed(BASH_HEAD);
        String tmpLog = newTmpLog();
        fileBuilder.appendWithLineFeed(createFun_print_and_clear(tmpLog));
        fileBuilder.appendWithLineFeed(createFullBackupCommand(parameters, tmpLog));

        return new File(".");
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
        builder.appendWithLineFeed("print_and_clear").appendWithLineFeed("exit 1");
        builder.append("fi");
        builder.appendWithLineFeed("}");
        return builder.toString();
    }

    private String createFullBackupCommand(Map<String, String> parameters, String tmpLog) {
        ScriptStringBuilder builder = new ScriptStringBuilder();
        builder.appendWithWhitespace(COMMAND_XTRABACKUP);
        // TODO

        builder.appendWithWhitespace(_REDIRECT).appendWithWhitespace(tmpLog).appendWithWhitespace(STANDARD_OUTPUT);
        return builder.toString();
    }
}
