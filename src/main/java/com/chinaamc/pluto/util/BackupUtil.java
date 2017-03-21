package com.chinaamc.pluto.util;

import com.chinaamc.pluto.backup.Backup;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

public class BackupUtil {

    public static final Pattern BACKUP_DIR_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}");

    public static boolean isFullBackup(Backup backup) {
        int backupType = backup.getBackupType();
        return backupType == Constants.BACKUP_TYPE_FULL;
    }

    public static File getLatestXtrabackupDirectory(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("directory doesn't exist");
        }
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() &&
                        BACKUP_DIR_PATTERN.matcher(pathname.getName()).matches();
            }
        });
        if (files == null || files.length == 0) {
            return null;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return (int) (getTimestampFromBackupFile(file2) - getTimestampFromBackupFile(file1));
            }
        });
        return files[0];
    }

    public static File getLatestBackupLogFile(File directory) {
        if (!directory.exists()) {
            throw new IllegalArgumentException("directory doesn't exist");
        }
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.startsWith("fullBackup") || name.startsWith("incrBackup");
            }
        });
        if (files == null || files.length == 0) {
            return null;
        }
        Arrays.sort(files, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                return (int) (file2.lastModified() - file1.lastModified());
            }
        });
        return files[0];
    }

    public static final String DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";

    public static long getTimestampFromBackupFile(File file) {
        String name = file.getName();
        try {
            return FastDateFormat.getInstance(DATETIME_FORMAT).parse(name).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getBackupType(File backupLogFile) {
        String fileName = backupLogFile.getName();
        if (fileName.startsWith("fullBackup")) {
            return Constants.BACKUP_TYPE_FULL;
        }
        if (fileName.startsWith("incrBackup")) {
            return Constants.BACKUP_TYPE_INCR;
        } else {
            throw new IllegalArgumentException("input logFile is not a backup log file, " + fileName);
        }
    }
}
