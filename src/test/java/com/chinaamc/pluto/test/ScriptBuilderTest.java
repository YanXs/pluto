package com.chinaamc.pluto.test;

import com.chinaamc.pluto.script.ScriptBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ScriptBuilderTest {

    @Test
    public void test_build_script() {
        ScriptBuilder scriptBuilder = new ScriptBuilder("/pluto/bin/fullBackup.sh");
        scriptBuilder.appendArg("/backup");
        String[] cmd = scriptBuilder.build();
        Assert.assertArrayEquals(cmd, new String[]{"/bin/sh", "-c", "/pluto/bin/fullBackup.sh /backup"});
    }
}
