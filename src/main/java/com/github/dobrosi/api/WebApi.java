package com.github.dobrosi.api;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.dobrosi.client.DelugeClient;
import com.github.dobrosi.client.Response;

import static java.util.Collections.emptyList;

public class WebApi extends DelugeApi {
    public WebApi(final DelugeClient delugeClient) {
        super(delugeClient);
    }

    public boolean connected() {
        return (boolean) call("web.connected");
    }

    public String downloadTorrentFromUrl(final String... url) {
        return (String) call("web.download_torrent_from_url", url);
    }

    public List<Object> addTorrents(String... paths) {
        if (paths == null || paths.length == 0) {
            return emptyList();
        }
        return Arrays.stream(paths).map(path -> {
            final GetTorrentInfoResponse torrentInfo = getTorrentInfo(path);
            if (torrentInfo.result instanceof Boolean) {
                return new Object[]{path, null};
            }
            Object infoHash = ((Map<?, ?>)torrentInfo.result).get("info_hash");
            final Map<Object, Object> torrents = updateUi().result.torrents;
            return new Object[]{ path, torrents != null && torrents.containsKey(infoHash) ? infoHash : null};
        }).map(o -> {
            if (o[1] != null) {
                return o[1];
            }
            return ((List<List<Object>>)call(
                "web.add_torrents",
                List.of(
                    Map.of("path", o[0],
                           "options", new Object())))).get(0).get(1);
        }).collect(Collectors.toUnmodifiableList());
    }

    public static class GetTorrentInfoResponse extends Response {
    }

    public GetTorrentInfoResponse getTorrentInfo(String filename) {
        return call(GetTorrentInfoResponse.class,"web.get_torrent_info", filename);
    }

    public static class GetTorrentFilesResponse extends Response<GetTorrentFilesResponse.Result> {
        public static class Result {
            public enum Type {
                dir, file
            }
            public static class File extends GetTorrentFilesResponse.Result {
                public int index;
                public long offset;
                public String path;
                public int priority;
                public double progress;
                public double[] progresses;
                public long size;
            }
            public Type type;
            public Map<String, File> contents;
        }
    }

    public GetTorrentFilesResponse getTorrentFiles(final Object id) {
        return call(GetTorrentFilesResponse.class, "web.get_torrent_files", id);
    }

    public static class UpdateUiResponse extends Response<UpdateUiResponse.Result> {
        public static class Result {
            public Map<Object, Object> torrents;
        }
    }

    public UpdateUiResponse updateUi() {
        return call(
            UpdateUiResponse.class,
            "web.update_ui",
            List.of(
                "queue",
                "name",
                "total_wanted",
                "state",
                "progress",
                "num_seeds",
                "total_seeds",
                "num_peers",
                "total_peers",
                "download_payload_rate",
                "upload_payload_rate",
                "eta",
                "ratio",
                "distributed_copies",
                "is_auto_managed",
                "time_added",
                "tracker_host",
                "download_location",
                "last_seen_complete",
                "total_done",
                "total_uploaded",
                "max_download_speed",
                "max_upload_speed",
                "seeds_peers_ratio",
                "total_remaining",
                "completed_time",
                "time_since_transfer"),
                    new Object());
    }
}
