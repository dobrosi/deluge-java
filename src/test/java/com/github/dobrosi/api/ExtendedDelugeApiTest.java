package com.github.dobrosi.api;

import org.junit.jupiter.api.Test;

class ExtendedDelugeApiTest {
    static final String TORRENT_URL = "https://ncore.pro/torrents.php?action=download&id=2070185&key=53af43571fb5a886b9e019eb4714c18b";
    ExtendedDelugeApi extendedDelugeApi = new ExtendedDelugeApi("secret");

    @Test
    void test() {
        Object id = extendedDelugeApi.downloadAndAddTorrentFromUrl(TORRENT_URL);
        System.out.println(
            "download: " + id + " filename: " + extendedDelugeApi.webApi.getTorrentFiles(id).result.contents.keySet()
                .iterator()
                .next());

        extendedDelugeApi.getAllTorrentIds().forEach(i -> System.out.println("Id: " + i));
        extendedDelugeApi.pauseAllTorrents();
        extendedDelugeApi.removeAllTorrents();
    }

    @Test
    void testGetLargestFile() {
        extendedDelugeApi.getAllTorrentIds().forEach(
            i -> System.out.println("File: " + extendedDelugeApi.getLargestFile(
                extendedDelugeApi.webApi.getTorrentFiles(i).result.contents.values()
            ).path)
        );
    }
}