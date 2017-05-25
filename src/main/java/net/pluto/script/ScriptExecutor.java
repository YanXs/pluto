package net.pluto.script;

import net.pluto.exceptions.ScriptExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class ScriptExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public int execute(ScriptBuilder scriptBuilder) {
        Runtime rt = Runtime.getRuntime();
        Process pro = null;
        try {
            String[] cmd = scriptBuilder.build();
            LOGGER.info("start executing cmd " + scriptBuilder.toString());
            InputStreamReader ir = null;
            LineNumberReader input = null;
            try {
                pro = rt.exec(cmd);
                ir = new InputStreamReader(pro.getInputStream());
                input = new LineNumberReader(ir);
                String line;
                while ((line = input.readLine()) != null) {
                    LOGGER.info(line);
                }
                if (pro.waitFor() == 0) {
                    return 0;
                } else {
                    throw new ScriptExecutionException("execute cmd :" + scriptBuilder.toString() + " failed");
                }
            } finally {
                if (input != null) {
                    input.close();
                }
            }
        } catch (Exception e) {
            throw new ScriptExecutionException(e);
        }
    }
}
