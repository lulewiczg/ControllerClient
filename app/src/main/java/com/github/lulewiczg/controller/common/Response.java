package com.github.lulewiczg.controller.common;

import java.io.Serializable;

public class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    private Status status;

    private String exceptionStr;

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, Exception exception) {
        this.status = status;
        if (exception != null) {
            this.exceptionStr = exception.toString();
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getExceptionStr() {
        return exceptionStr;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Response: ").append(status);
        if (exceptionStr != null) {
            str.append(", cause:\n").append(exceptionStr);
        }
        return str.toString();
    }
}
