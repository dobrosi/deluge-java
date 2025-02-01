package com.github.dobrosi.api;

import com.github.dobrosi.client.DelugeClient;

public class AuthApi extends DelugeApi {
    public AuthApi(final DelugeClient delugeClient) {
        super(delugeClient);
    }

    public boolean login(String password) {
        return (boolean) call("auth.login", password);
    }
}
