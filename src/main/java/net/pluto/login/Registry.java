package net.pluto.login;

import java.util.List;

public interface Registry {

    void createUser(User user);

    void closeUser(User user);

    List<User> load();
}
