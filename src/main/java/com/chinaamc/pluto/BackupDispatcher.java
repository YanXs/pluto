package com.chinaamc.pluto;

import com.chinaamc.pluto.backup.Backup;
import com.chinaamc.pluto.backup.BackupExecutor;
import com.chinaamc.pluto.backup.BackupType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/pluto")
public class BackupDispatcher {

    private volatile boolean isBackingUp;

    private volatile boolean isRestore;

    @Autowired
    private BackupExecutor backupExecutor;


    @RequestMapping("full/backup")
    public GenericResult fullBackup(@RequestParam("name") String name) {
        return doBackup(name, BackupType.Full, Collections.EMPTY_LIST);
    }

    @RequestMapping("incremental/backup")
    public GenericResult incrementalBackup(@RequestParam("name") String name) {
        return doBackup(name, BackupType.Incremental, Collections.EMPTY_LIST);
    }

    @RequestMapping("partial/backup")
    public GenericResult partialBackup(@RequestParam("name") String name,
                                       @RequestParam(value = "database") String database) {
        return doBackup(name, BackupType.Partial, Collections.singletonList(database));
    }

    private GenericResult doBackup(String name, BackupType backupType, List<String> databases) {
        GenericResult result = new GenericResult();
        if (isBackingUp) {
            result.setCode(GenericResult.CODE_PENDING);
            result.setMessage("is backingUp, please wait");
            return result;
        }
        Backup.Builder builder = new Backup.Builder();
        try {
            backupExecutor.executeBackup(builder.name(name).backupType(backupType).build(), databases);
            result.setCode(GenericResult.CODE_OK);
            result.setMessage("backup completed");
        } catch (Exception e) {
            result.setCode(GenericResult.CODE_ERROR);
            result.setMessage(e.getMessage());
        } finally {
            isBackingUp = false;
        }
        return result;
    }

    @RequestMapping("rollback")
    public GenericResult rollback(@RequestParam("id") String id) {
        GenericResult result = new GenericResult();
        if (isRestore) {
            result.setCode(GenericResult.CODE_PENDING);
            result.setMessage("is restoring, please wait");
            return result;
        }
        try {
            long start = System.currentTimeMillis();
            if (backupExecutor.executeRollback(id)) {
                result.setCode(GenericResult.CODE_OK);
                long duration = (System.currentTimeMillis() - start) / 1000;
                result.setMessage("rollback completed in " + duration + " seconds");
            }
        } catch (Exception e) {
            result.setCode(GenericResult.CODE_ERROR);
            result.setMessage(e.getMessage());
        } finally {
            isRestore = false;
        }
        return result;
    }

    @RequestMapping(value = "/backups", method = RequestMethod.GET)
    public GenericResult getBackups() {
        GenericResult result = new GenericResult();
        List<Backup> backups = backupExecutor.getBackups();
        result.setCode(GenericResult.CODE_OK);
        if (CollectionUtils.isEmpty(backups)) {
            result.setMessage("does not have any backup");
        } else {
            result.setMessage("there are " + backups.size() + " backups");
        }
        result.setContent(backups);
        return result;
    }

    @RequestMapping(value = "/delete")
    public GenericResult deleteBackup(@RequestParam(value = "ids[]") List<String> ids) {
        GenericResult result = new GenericResult();
        if (ids.size() == 0) {
            result.setCode(GenericResult.CODE_ERROR);
            result.setMessage("ids is empty");
        } else {
            boolean deleteSucceeded = false;
            try {
                for (String id : ids) {
                    backupExecutor.removeBackup(Long.valueOf(id));
                }
                deleteSucceeded = true;
            } catch (Exception e) {
                result.setCode(GenericResult.CODE_ERROR);
                result.setMessage(e.getMessage());
            }
            if (deleteSucceeded) {
                result.setCode(GenericResult.CODE_OK);
                result.setMessage("all chosen backups were deleted");
            }
        }
        return result;
    }
}
