package com.chinaamc.pluto.login;

import com.chinaamc.pluto.util.EnDecryptUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserManager {

    private static final Map<String, User> store = new HashMap<>();

    @Autowired
    private Registry registry;

    //@PostConstruct
    public void load() {
        List<User> userList = registry.load();
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        for (User user : userList) {
            store.put(user.getUsername(), user);
        }
    }

    public void createUser(String username, String password) {
        if (usernameExists(username)) {
            throw new IllegalArgumentException("user: [" + username + "] already registered");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(encrypt(password));
        registry.createUser(user);
        store.put(user.getUsername(), user);
    }

    public void closeUser(String username) {

    }

    public boolean usernameExists(String name) {
        load();
        return store.containsKey(name);
    }

    public void validateUser(String username, String password) {
        load();
        User user = store.get(username);
        if (user == null) {
            throw new IllegalStateException("user: [" + username + "] doesn't exist");
        }
        if (!password.equals(decrypt(user.getPassword()))) {
            throw new IllegalArgumentException("wrong password");
        }
    }

    public String encrypt(String password) {
        try {
            return EnDecryptUtil.encrypt(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String password) {
        try {
            return EnDecryptUtil.decrypt(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
