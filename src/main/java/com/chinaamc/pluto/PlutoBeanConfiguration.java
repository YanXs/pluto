package com.chinaamc.pluto;

import com.chinaamc.pluto.backup.*;
import com.chinaamc.pluto.backup.store.BackupStore;
import com.chinaamc.pluto.backup.store.DelegatingBackupStore;
import com.chinaamc.pluto.backup.store.FlatFileBackupStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlutoBeanConfiguration {

    @Bean
    public BackupStore delegatingBackupStore(BackupStore persistBackupStore) {
        return new DelegatingBackupStore(persistBackupStore);
    }

    @Bean
    public BackupStore persistBackupStore() {
        return new FlatFileBackupStore(BackupCodec.JSON);
    }

    @Bean
    public BackupExecutor backupExecutor(BackupStore delegatingBackupStore) {
        return new BackupExecutor(delegatingBackupStore);
    }
}
