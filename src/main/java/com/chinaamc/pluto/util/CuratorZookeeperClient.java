package com.chinaamc.pluto.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CuratorZookeeperClient implements ZooKeeperClient {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final CuratorFramework client;

    private volatile boolean closed = false;

    public CuratorZookeeperClient(String connectionString) {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .sessionTimeoutMs(20000)
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE, 1000))
                .connectionTimeoutMs(5000);
        client = builder.build();
        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework client, ConnectionState state) {
                if (state == ConnectionState.LOST) {
                    LOGGER.warn("Connection lost");
                } else if (state == ConnectionState.CONNECTED) {
                    LOGGER.info("Connection succeeded");
                } else if (state == ConnectionState.RECONNECTED) {
                    LOGGER.info("Connection reconnected");
                }
            }
        });
        client.start();
    }

    private void createEphemeral(String path) {
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (KeeperException.NodeExistsException ignored) {
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void createPersistent(String path) {
        try {
            client.create().forPath(path);
        } catch (KeeperException.NodeExistsException ignored) {
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void create(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    public void delete(String path) {
        try {
            client.delete().forPath(path);
        } catch (KeeperException.NoNodeException ignored) {
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public List<String> getChildren(String path) {
        try {
            return client.getChildren().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public List<String> getChildren(String path, CuratorWatcher watcher) {
        try {
            return client.getChildren().usingWatcher(watcher).forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] getData(String path) {
        try {
            return client.getData().forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public byte[] getData(String path, CuratorWatcher watcher) {
        try {
            return client.getData().usingWatcher(watcher).forPath(path);
        } catch (KeeperException.NoNodeException e) {
            return null;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void setData(String path, byte[] data) {
        try {
            client.setData().forPath(path, data);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public CuratorFramework getRealClient() {
        return this.client;
    }

    @Override
    public boolean exists(String path) {
        try {
            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isConnected() {
        return client.getZookeeperClient().isConnected();
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        client.close();
    }
}
