package com.chinaamc.pluto.backup;

public class BackupEnvironment {

    private int id;

    private String scheme;

    private String username;

    private String password;

    private int port;

    private String dataDir;

    private String dataBakDir;

    private String backupDir;

    private String backupLog;

    private String backupLogBak;

    private String xtrabackupMemory;

    private String xtrabackupParallel;

    private String xtrabackupLogInfo;

    private String startupCommand;

    private String shutdownCommand;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getDataBakDir() {
        return dataBakDir;
    }

    public void setDataBakDir(String dataBakDir) {
        this.dataBakDir = dataBakDir;
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public String getBackupLog() {
        return backupLog;
    }

    public void setBackupLog(String backupLog) {
        this.backupLog = backupLog;
    }

    public String getBackupLogBak() {
        return backupLogBak;
    }

    public void setBackupLogBak(String backupLogBak) {
        this.backupLogBak = backupLogBak;
    }

    public String getXtrabackupMemory() {
        return xtrabackupMemory;
    }

    public void setXtrabackupMemory(String xtrabackupMemory) {
        this.xtrabackupMemory = xtrabackupMemory;
    }

    public String getXtrabackupParallel() {
        return xtrabackupParallel;
    }

    public void setXtrabackupParallel(String xtrabackupParallel) {
        this.xtrabackupParallel = xtrabackupParallel;
    }

    public String getXtrabackupLogInfo() {
        return xtrabackupLogInfo;
    }

    public void setXtrabackupLogInfo(String xtrabackupLogInfo) {
        this.xtrabackupLogInfo = xtrabackupLogInfo;
    }

    public String getStartupCommand() {
        return startupCommand;
    }

    public void setStartupCommand(String startupCommand) {
        this.startupCommand = startupCommand;
    }

    public String getShutdownCommand() {
        return shutdownCommand;
    }

    public void setShutdownCommand(String shutdownCommand) {
        this.shutdownCommand = shutdownCommand;
    }
}
