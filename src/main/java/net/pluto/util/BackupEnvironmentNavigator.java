package net.pluto.util;

import net.pluto.backup.BackupEnvironment;

import java.util.HashMap;
import java.util.Map;

public class BackupEnvironmentNavigator {

    private final Map<String, BackupEnvironment> navigator = new StrictMap<>();


    public void add(BackupEnvironment backupEnvironment) {
        navigator.put(backupEnvironment.getScheme(), backupEnvironment);
    }

    public BackupEnvironment get(String key) {
        return navigator.get(key);
    }

    static class StrictMap<K, V> extends HashMap<K, V> {

        public V put(K key, V value) {
            if (containsKey(key)) {
                throw new StrictMapException("object with key: " + key + " existed");
            }
            return super.put(key, value);
        }

        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new StrictMapException("object with key: " + key + " does not exist");
            }
            return value;
        }
    }

    static class StrictMapException extends RuntimeException {
        public StrictMapException(String message) {
            super(message);
        }
    }
}
