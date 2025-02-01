package com.github.dobrosi.api;

import java.io.ByteArrayOutputStream;

import com.github.dobrosi.client.DelugeClient;
import com.github.dobrosi.client.Request;
import com.github.dobrosi.client.Response;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelugeApi {
    private final Logger logger = LoggerFactory.getLogger(DelugeApi.class);

    protected final DelugeClient delugeClient;

    public DelugeApi(DelugeClient delugeClient) {
        this.delugeClient = delugeClient;
    }

    public <T extends Request, U extends Response> U call(Class<U> responseClass, T request) {
        final Gson gson = new Gson();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String requestString = gson.toJson(request);
        logger.debug("request: {}", requestString);
        delugeClient.postJson(byteArrayOutputStream, requestString);
        final String result = byteArrayOutputStream.toString();
        logger.debug("result: {}", result);
        U response = gson.fromJson(result, responseClass);
        if (response.error != null) {
            throw new DelugeApiException(response);
        }
        return response;
    }

    public <T extends  Request, U extends Response> U call(T request) {
        return  (U) call(Response.class, request);
    }

    public Object call(String method, Object... params) {
        return call(new Request(method, params)).result;
    }

    public <U extends Response> U call(Class<U> responseClass, String method, Object... params) {
        return call(responseClass, new Request(method, params));
    }
}
