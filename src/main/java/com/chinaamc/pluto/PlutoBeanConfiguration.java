package com.chinaamc.pluto;

import com.chinaamc.pluto.backup.BackupCodec;
import com.chinaamc.pluto.backup.BackupExecutor;
import com.chinaamc.pluto.backup.store.BackupStore;
import com.chinaamc.pluto.backup.store.DelegatingBackupStore;
import com.chinaamc.pluto.backup.store.FlatFileBackupStore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
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

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .create();
    }

    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.setSessionTimeout(60 * 60);
            }
        };
    }

}
