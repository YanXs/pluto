package net.pluto.exceptions;

public class ScriptExecutionException extends RuntimeException{
    public ScriptExecutionException(String message) {
        super(message);
    }

    public ScriptExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptExecutionException(Throwable cause) {
        super(cause);
    }
}
