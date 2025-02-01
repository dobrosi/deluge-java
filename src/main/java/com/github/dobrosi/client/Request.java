package com.github.dobrosi.client;

public class Request {
    public int id = 1;
    public String method;
    public Object[] params;

    public Request(final String method, final Object... params) {
        this.method = method;
        this.params = params;
    }
}
