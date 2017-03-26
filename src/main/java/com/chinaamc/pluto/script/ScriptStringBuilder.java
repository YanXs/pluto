package com.chinaamc.pluto.script;

public class ScriptStringBuilder {

    public static final String LINEFEED = System.lineSeparator();
    public static final String WHITESPACE = " ";

    private final StringBuilder delegateBuilder = new StringBuilder();

    public ScriptStringBuilder append(String s) {
        delegateBuilder.append(s);
        return this;
    }

    public ScriptStringBuilder appendWithLineFeed(String string) {
        delegateBuilder.append(string).append(LINEFEED);
        return this;
    }

    public ScriptStringBuilder appendWithWhitespace(String string) {
        delegateBuilder.append(string).append(WHITESPACE);
        return this;
    }

    @Override
    public String toString() {
        return delegateBuilder.toString();
    }
}
