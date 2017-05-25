package net.pluto.backup;

public class BackupEnvironment {

    private final String instance;

    private final String username;

    private final String password;

    private final int port;

    private final String mysqlGroup;

    private final String mysqlUser;

    private final String dataDir;

    private final String dataBakDir;

    private final String xtrabackupMemory;

    private final String xtrabackupParallel;

    private final String xtrabackupLogInfo;

    private final String defaultsFile;

    private final String startupCommand;

    private final String shutdownCommand;

    public BackupEnvironment(Builder builder) {
        this.instance = builder.instance;
        this.username = builder.username;
        this.password = builder.password;
        this.port = builder.port;
        this.mysqlGroup = builder.mysqlGroup;
        this.mysqlUser = builder.mysqlUser;
        this.dataDir = builder.dataDir;
        this.dataBakDir = builder.dataBakDir;
        this.xtrabackupMemory = builder.xtrabackupMemory;
        this.xtrabackupParallel = builder.xtrabackupParallel;
        this.xtrabackupLogInfo = builder.xtrabackupLogInfo;
        this.defaultsFile = builder.defaultsFile;
        this.startupCommand = builder.startupCommand;
        this.shutdownCommand = builder.shutdownCommand;
    }

    public static class Builder {
        private String instance;
        private String username;
        private String password;
        private int port;
        private String mysqlGroup;
        private String mysqlUser;
        private String dataDir;
        private String dataBakDir;
        private String xtrabackupMemory;
        private String xtrabackupParallel;
        private String xtrabackupLogInfo;
        private String defaultsFile;
        private String startupCommand;
        private String shutdownCommand;

        public Builder() {
        }

        public Builder instance(String instance) {
            this.instance = instance;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder mysqlGroup(String mysqlGroup) {
            this.mysqlGroup = mysqlGroup;
            return this;
        }

        public Builder mysqlUser(String mysqlUser) {
            this.mysqlUser = mysqlUser;
            return this;
        }

        public Builder dataDir(String dataDir) {
            this.dataDir = dataDir;
            return this;
        }

        public Builder dataBakDir(String dataBakDir) {
            this.dataBakDir = dataBakDir;
            return this;
        }

        public Builder xtrabackupMemory(String xtrabackupMemory) {
            this.xtrabackupMemory = xtrabackupMemory;
            return this;
        }

        public Builder xtrabackupParallel(String xtrabackupParallel) {
            this.xtrabackupParallel = xtrabackupParallel;
            return this;
        }

        public Builder xtrabackupLogInfo(String xtrabackupLogInfo) {
            this.xtrabackupLogInfo = xtrabackupLogInfo;
            return this;
        }

        public Builder defaultsFile(String defaultsFile) {
            this.defaultsFile = defaultsFile;
            return this;
        }

        public Builder startupCommand(String startupCommand) {
            this.startupCommand = startupCommand;
            return this;
        }

        public Builder shutdownCommand(String shutdownCommand) {
            this.shutdownCommand = shutdownCommand;
            return this;
        }

        public BackupEnvironment build() {
            return new BackupEnvironment(this);
        }
    }

    public String getInstance() {
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getMysqlGroup() {
        return mysqlGroup;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public String getDataDir() {
        return dataDir;
    }

    public String getDataBakDir() {
        return dataBakDir;
    }

    public String getXtrabackupMemory() {
        return xtrabackupMemory;
    }

    public String getXtrabackupParallel() {
        return xtrabackupParallel;
    }

    public String getXtrabackupLogInfo() {
        return xtrabackupLogInfo;
    }


    public String getDefaultsFile() {
        return defaultsFile;
    }

    public String getStartupCommand() {
        return startupCommand;
    }

    public String getShutdownCommand() {
        return shutdownCommand;
    }
}