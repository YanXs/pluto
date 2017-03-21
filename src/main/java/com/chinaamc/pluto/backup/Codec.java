package com.chinaamc.pluto.backup;

import java.util.List;

public interface Codec {

    JsonCodec JSON = new JsonCodec();

    Backup readBackup(byte[] bytes);

    byte[] writeBackup(Backup value);

    List<Backup> readBackups(byte[] bytes);

    byte[] writeBackups(List<Backup> value);
}
