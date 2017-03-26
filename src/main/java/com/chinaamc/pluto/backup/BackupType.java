package com.chinaamc.pluto.backup;

/**
 * @author Xs.
 */
public enum BackupType {

    Full("Full"),

    Partial("Partial"),

    Incremental("Incremental");

    private String type;

    BackupType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
