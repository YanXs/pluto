package com.chinaamc.pluto.util;

import java.io.Closeable;
import java.util.List;

public interface ZooKeeperClient extends Closeable{

    void create(String path, boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    byte[] getData(String path);

    void setData(String path, byte[] data);

    boolean exists(String path);

    boolean isConnected();

    void close();
}
