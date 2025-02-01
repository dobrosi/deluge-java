package com.github.dobrosi.api;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chameleon.playlist.xspf.Location;
import chameleon.playlist.xspf.Playlist;
import chameleon.playlist.xspf.Track;
import com.github.dobrosi.client.DelugeClient;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

public class ExtendedDelugeApi extends DelugeApi {
    public final WebApi webApi;

    public final CoreApi coreApi;

    public ExtendedDelugeApi(DelugeClient delugeClient, String password) {
        super(delugeClient);
        new AuthApi(delugeClient).login(password);
        webApi = new WebApi(delugeClient);
        coreApi = new CoreApi(delugeClient);
    }

    public ExtendedDelugeApi(String password) {
        this(new DelugeClient(), password);
    }

    public ExtendedDelugeApi(URL baseUrl, String password) {
        this(new DelugeClient(baseUrl), password);
    }

    public Object downloadAndAddTorrentFromUrl(String torrentFileUrl) {
        return webApi.addTorrents(webApi.downloadTorrentFromUrl(torrentFileUrl)).get(0);
    }

    public List<Object> downloadAndAddTorrentFromUrls(String... torrentFileUrls) {
        if(torrentFileUrls == null || torrentFileUrls.length == 0) {
            return emptyList();
        }
        return stream(torrentFileUrls).map(this::downloadAndAddTorrentFromUrl).toList();
    }

    public Set<Object> getAllTorrentIds() {
        return webApi.updateUi().result.torrents.keySet();
    }

    public void pauseAllTorrents() {
        getAllTorrentIds().forEach(coreApi::pauseTorrent);
    }

    public void removeAllTorrents() {
        getAllTorrentIds().forEach(coreApi::removeTorrent);
    }

    public WebApi.GetTorrentFilesResponse.Result.File getLargestFile(
        Collection<WebApi.GetTorrentFilesResponse.Result.File> dir) {
        return getLargestFile(dir, null);
    }

    public WebApi.GetTorrentFilesResponse.Result.File getFileByIndex(final WebApi.GetTorrentFilesResponse.Result dir, final int fileIndex) {
        return getFileByIndex(dir.contents.values(), fileIndex);
    }

    public String createPlaylist(
        String url,
        Map<Object, WebApi.GetTorrentFilesResponse.Result> files) {

        StringBuffer out = new StringBuffer("#EXTM3U\n\n");
        files.keySet().forEach(id -> createPlaylist(format("%s/%s", url, id), files.get(id).contents.values(), out));
        return out.toString();
    }

    private void createPlaylist(
        String url,
        Collection<WebApi.GetTorrentFilesResponse.Result.File> dir,
        StringBuffer out) {

        for(WebApi.GetTorrentFilesResponse.Result.File next : dir) {
            if (next.type == WebApi.GetTorrentFilesResponse.Result.Type.dir) {
                createPlaylist(url, next.contents.values(), out);
            } else {
                WebApi.GetTorrentFilesResponse.Result.File file = next;
                out
                    .append(format("#EXTINF:id=%s,%s\n", out.length(), file.path))
                    .append(format("%s/%s\n\n", url, file.index));
            }
        }
    }

    public Playlist createXspfPlaylist(
        String url,
        Map<Object, WebApi.GetTorrentFilesResponse.Result> files) {

        Playlist playlist = new Playlist();
        files.keySet().forEach(id -> createXspfPlaylist(format("%s/%s", url, id), files.get(id).contents.values(), playlist));
        return playlist;
    }

    private void createXspfPlaylist(
        String url,
        Collection<WebApi.GetTorrentFilesResponse.Result.File> dir,
        Playlist playlist) {

        for(WebApi.GetTorrentFilesResponse.Result.File next : dir) {
            if (next.type == WebApi.GetTorrentFilesResponse.Result.Type.dir) {
                createXspfPlaylist(url, next.contents.values(), playlist);
            } else {
                WebApi.GetTorrentFilesResponse.Result.File file = next;
                Track track = new Track();
                Location location = new Location();
                location.setText(format("%s/%s", url, file.index));
                track.setTitle(file.path);
                track.addStringContainer(location);
                playlist.addTrack(track);
            }
        }
    }

    private WebApi.GetTorrentFilesResponse.Result.File getFileByIndex(
        Collection<WebApi.GetTorrentFilesResponse.Result.File> dir,
        int fileIndex) {
        WebApi.GetTorrentFilesResponse.Result.File result;
        for(WebApi.GetTorrentFilesResponse.Result.File next : dir) {
            if (next.type == WebApi.GetTorrentFilesResponse.Result.Type.dir) {
                result = getFileByIndex(next.contents.values(), fileIndex);
                if (result != null) {
                    return result;
                }
            } else {
                WebApi.GetTorrentFilesResponse.Result.File file = next;
                if (file.index == fileIndex) {
                    return file;
                }
            }
        }
        return null;
    }

    private WebApi.GetTorrentFilesResponse.Result.File getLargestFile(
        Collection<WebApi.GetTorrentFilesResponse.Result.File> dir,
        WebApi.GetTorrentFilesResponse.Result.File maxFile) {

        for(WebApi.GetTorrentFilesResponse.Result.File next : dir) {
            if (next.type == WebApi.GetTorrentFilesResponse.Result.Type.dir) {
                maxFile = getLargestFile(next.contents.values(), maxFile);
            } else {
                WebApi.GetTorrentFilesResponse.Result.File file = next;
                if (maxFile == null || maxFile.size < file.size) {
                    maxFile = file;
                }
            }
        }
        return maxFile;
    }
}
