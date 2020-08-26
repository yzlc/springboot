package xyz.yzlc.common.model.http;

import org.apache.http.HttpStatus;

public enum HttpStatusEnum implements HttpStatus {
    OK(SC_OK, "OK"),
    BAD_REQUEST(SC_BAD_REQUEST, "Bad Request"),
    INTERNAL_SERVER_ERROR(SC_INTERNAL_SERVER_ERROR, "Internal Server Error");

    private final int code;
    private final String msg;

    HttpStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "HttpStatus{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
