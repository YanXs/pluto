package net.pluto.script;

import net.pluto.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ScriptBuilder {

    private final StringBuilder builder = new StringBuilder();

    private final List<String> commandLine = new ArrayList<>();

    public ScriptBuilder(String path) {
        commandLine.add("/bin/sh");
        commandLine.add("-c");
        builder.append(path);
    }

    public void appendArg(String arg) {
        builder.append(Constants.WHITESPACE).append(arg);
    }

    public String[] build() {
        commandLine.add(builder.toString());
        return commandLine.toArray(new String[commandLine.size()]);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String str : commandLine) {
            builder.append(str).append(Constants.WHITESPACE);
        }
        return builder.toString();
    }
}
