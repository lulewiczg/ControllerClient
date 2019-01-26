package com.github.lulewiczg.controller.exception;

import com.github.lulewiczg.controller.common.Response;

/**
 * Created by Grzegurz on 2016-04-04.
 */
public class ConnectException extends RuntimeException {

    private Response response;

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public ConnectException(Response response) {
        this.response = response;
    }
}
