package net.pluto;

import java.io.Serializable;

public class GenericResult implements Serializable{

    public static final String CODE_OK = "0000";

    public static final String CODE_ERROR = "1000";

    public static final String CODE_PENDING = "2000";

    private String code;

    private String message;

    private Object content;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
