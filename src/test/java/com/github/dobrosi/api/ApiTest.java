package com.github.dobrosi.api;

import java.util.Map;

import com.github.dobrosi.client.DelugeClient;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiTest {
    static final String TORRENT_URL = "https://ncore.pro/torrents.php?action=download&id=2070185&key=53af43571fb5a886b9e019eb4714c18b";
    Gson gson = new Gson();
    DelugeClient delugeClient;
    AuthApi authApi;
    WebApi webApi;
    CoreApi coreApi;

    @BeforeEach
    void setUp() {
         delugeClient = new DelugeClient();
         authApi = new AuthApi(delugeClient);
         webApi = new WebApi(delugeClient);
         coreApi = new CoreApi(delugeClient);
        System.out.println("Login: " + authApi.login("secret"));
        System.out.println("Connected: " + webApi.connected());
    }

    @Test
    void test() {

        String torrentPath = downloadTorrentFromUrl();
        System.out.println("Downloaded " + torrentPath);
        Object id = webApi.addTorrents(torrentPath).get(0);

        System.out.println("Torrent files: " +  gson.toJson(webApi.getTorrentFiles(id)));

        System.out.println("Update UI: " +  gson.toJson(webApi.updateUi()));

        System.out.println("Pause " + coreApi.pauseTorrent(id));
        System.out.println("Remove " + coreApi.removeTorrent(id));
    }

    private String downloadTorrentFromUrl() {
        return webApi.downloadTorrentFromUrl(TORRENT_URL);
    }

    @Test
    void updateUi() {
        System.out.println("Update UI: " +  gson.toJson(webApi.updateUi()));
    }

    @Test
    void getTorrentInfo() {
        WebApi.GetTorrentInfoResponse result = webApi.getTorrentInfo(downloadTorrentFromUrl());
        System.out.println("Torrent info: " + result);
        assertNotNull(result);
        assertEquals("70948f23941b4c66447ac6b06f80bdbf8d6aba67", ((Map<?, ?>)result.result).get("info_hash"));
    }

    @Test
    void getTorrentInfoNotExists() {
        WebApi.GetTorrentInfoResponse result = webApi.getTorrentInfo("not-exists");
        System.out.println("Torrent info: " +  result);
        assertNotNull(result);
        assertInstanceOf(Boolean.class, result.result);
    }
}