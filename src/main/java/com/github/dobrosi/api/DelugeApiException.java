package com.github.dobrosi.api;

import com.github.dobrosi.client.Response;
import com.google.gson.Gson;

public class DelugeApiException extends RuntimeException {
    public final Response response;

    public DelugeApiException(final Response response) {
        super(new Gson().toJson(response));
        this.response = response;
    }
}
