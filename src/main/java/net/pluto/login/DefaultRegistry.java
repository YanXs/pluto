package net.pluto.login;

import net.pluto.util.Configuration;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class DefaultRegistry implements Registry {

    private final UserCodec codec = UserCodec.JSON;

    @Override
    public void createUser(User user) {
        String userFilePath = Configuration.getBackupUserFilePath();
        List<User> users = load();
        users.add(user);
        try {
            FileUtils.writeByteArrayToFile(new File(userFilePath), codec.writeUsers(users));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeUser(User user) {

    }

    @Override
    public List<User> load() {
        String userFilePath = Configuration.getBackupUserFilePath();
        File file = new File(userFilePath);
        if (!file.exists()){
            throw new IllegalStateException("pluto-users.json doesn't exist");
        }
        try {
            return codec.readUsers(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
