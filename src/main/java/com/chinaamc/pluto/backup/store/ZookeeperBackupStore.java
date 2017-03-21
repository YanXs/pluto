package com.chinaamc.pluto.backup.store;

import com.chinaamc.pluto.backup.Backup;
import com.chinaamc.pluto.backup.BackupCodec;
import com.chinaamc.pluto.util.Configuration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ZookeeperBackupStore extends AbstractBackupStore {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final BackupCodec backupCodec;

    private final CuratorFramework client;

    public ZookeeperBackupStore(BackupCodec backupCodec, String connectionString) {
        this.backupCodec = backupCodec;
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(20000)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000)
                .namespace("backup");
        client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework client, ConnectionState state) {
                if (state == ConnectionState.LOST) {
                    LOGGER.warn("connection lost");
                } else if (state == ConnectionState.CONNECTED) {
                    LOGGER.info("connection succeed");
                } else if (state == ConnectionState.RECONNECTED) {
                    LOGGER.info("connection reconnected");
                }
            }
        });
        client.start();
    }

    @Override
    public long append(Backup backup) {
        return backup.getTraceId();
    }

    @Override
    public synchronized void save(List<Backup> backups) {
        byte[] bytes = backupCodec.writeBackups(backups);
        String path = Configuration.getBackupLogFilePath();
        createPathIfNeeded(path);
        try {
            client.setData().forPath(path, bytes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void createPathIfNeeded(String path) {
        try {
            client.create().creatingParentsIfNeeded().forPath(path);
        } catch (KeeperException.NodeExistsException ignored) {
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public List<Backup> getBackups() {
        String path = Configuration.getBackupLogFilePath();
        createPathIfNeeded(path);
        byte[] bytes;
        try {
            bytes = client.getData().forPath(path);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        List<Backup> backups = backupCodec.readBackups(bytes);
        if (CollectionUtils.isEmpty(backups)) {
            return null;
        } else {
            return backups;
        }
    }

    @Override
    public void removeBackup(Backup backup) {

    }

}
