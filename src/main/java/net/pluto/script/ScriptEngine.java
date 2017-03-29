package net.pluto.script;

import net.pluto.exceptions.ScriptExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class ScriptEngine {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public int execute(ScriptBuilder scriptBuilder) {
        Runtime rt = Runtime.getRuntime();
        Process pro = null;
        try {
            String[] cmd = scriptBuilder.build();
            LOGGER.info("start executing cmd " + scriptBuilder.toString());

            pro = rt.exec(cmd);
            InputStreamReader ir = new InputStreamReader(pro.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                LOGGER.info(line);
            }
            if (pro.waitFor() == 0) {
                return 0;
            } else {
                throw new ScriptExecutionException("execute cmd :" + scriptBuilder.toString() + " failed");
            }
        } catch (Exception e) {
            throw new ScriptExecutionException(e);
        }
    }
}
