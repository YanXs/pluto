package com.chinaamc.pluto.backup;

public class BackupEnvironment {

    private int id;

    private String scheme;

    private int mysqlPort;

    private String dataDir;

    private String dataBakDir;

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

    public int getMysqlPort() {
        return mysqlPort;
    }

    public void setMysqlPort(int mysqlPort) {
        this.mysqlPort = mysqlPort;
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

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        BackupEnvironment that = (BackupEnvironment) object;
        return scheme.equals(that.scheme);

    }

    @Override
    public int hashCode() {
        return scheme.hashCode();
    }

    @Override
    public String toString() {
        return "BackupEnvironment{" +
                "id='" + id + '\'' +
                ", scheme='" + scheme + '\'' +
                ", mysqlPort='" + mysqlPort + '\'' +
                ", dataDir='" + dataDir + '\'' +
                ", dataBakDir='" + dataBakDir + '\'' +
                '}';
    }
}
