package com.github.dobrosi.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class DelugeClient {
    private static final URL DEFAULT_BASE_URL;

    static {
        try {
            DEFAULT_BASE_URL = new URL("http://localhost:8112");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URL baseUrl;

    private static String cookie;

    private HttpClient client = HttpClient.newHttpClient();

    public DelugeClient() {
        this.baseUrl = DEFAULT_BASE_URL;
    }

    public DelugeClient(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public DelugeClient postJson(OutputStream outputStream, String command) {
        try {
            final List<String> headers = new ArrayList<>();
            headers.add("Content-Type");
            headers.add("application/json");
            headers.add("Accept");
            headers.add("application/json");
            if (cookie != null) {
                headers.add("Cookie");
                headers.add(cookie);
            }
            final HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(String.format("%s/json", baseUrl)))
                .headers(headers.toArray(new String[0]))
                .POST(HttpRequest.BodyPublishers.ofString(command))
                .build();
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

            if (cookie == null) {
                final List<String> cookies = response.headers().allValues("set-cookie");
                if (!cookies.isEmpty()) {
                    cookie = cookies.get(0);
                }
            }
            outputStream.write(response.body().getBytes());
            outputStream.write("\n".getBytes());
            return this;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
