package com.github.dobrosi.client;

public class Response<T> {
    public static class Error {
        public String message;
        int code;
    }
    public int id;
    public Error error;
    public T result;
}
