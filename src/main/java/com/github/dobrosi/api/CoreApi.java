package com.github.dobrosi.api;

import java.util.List;

import com.github.dobrosi.client.DelugeClient;

public class CoreApi extends DelugeApi {
    public CoreApi(final DelugeClient delugeClient) {
        super(delugeClient);
    }

    public List<Object> removeTorrent(Object... ids) {
        return (List<Object>) call("core.remove_torrents", new Object[] { ids, true });
    }

    public Void pauseTorrent(Object... ids) {
        call("core.pause_torrents", new Object[] { ids });
        return null;
    }

    public Void resumeTorrent(Object... ids) {
        call("core.resume_torrents", new Object[] { ids });
        return null;
    }
}
