package com.chinaamc.pluto.script;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Xs.
 */
public class ScriptParameter {

    public static final String PARAM_USER = "--user";

    public static final String PARAM_PASSWORD = "--password";

    public static final String PARAM_DATABASE = "--databases";

    public static final String PARAM_MEMORY = "--use-memory";

    public static final String PARAM_PARALLEL = "--parallel";

    public static final String PARAM_INCREMENTAL = "--incremental";

    public static final String PARAM_INCREMENTAL_BASE = "--incremental-basedir";

    public static final String PARAM_APPLY_LOG = "--apply-log";

    public static final String PARAM_COPY_BACK = "--copy-back";

    public static final String PARAM_EXPORT = "--export";

    public static final String PARAM_BACKUP_DIR = "--backup-dir";

    public static final String PARAM_BASE_DIR = "--base-dir";

    private final Map<String, Pair> store = new LinkedHashMap<>();

    public void addPair(Pair pair) {
        store.put(pair.key, pair);
    }

    public Pair getPair(String key) {
        return store.get(key);
    }

    public Collection<Pair> values() {
        return store.values();
    }

    public Pair newPair() {
        return new Pair();
    }

    public static class Pair {
        private String key;
        private String value;
        private boolean keyVisible = true;
        private boolean valueVisible = true;

        public String key() {
            return key;
        }

        public String value() {
            return value;
        }

        public Pair key(String key) {
            this.key = key;
            return this;
        }

        public Pair keyVisible(boolean keyVisible) {
            this.keyVisible = keyVisible;
            return this;
        }

        public Pair value(String value) {
            this.value = value;
            return this;
        }

        public Pair valueVisible(boolean valueVisible) {
            this.valueVisible = valueVisible;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            return key.equals(pair.key);

        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (keyVisible) {
                builder.append(key);
                if (valueVisible) {
                    builder.append("=").append(value);
                }
            } else {
                builder.append(value);
            }
            return builder.toString();
        }
    }
}
