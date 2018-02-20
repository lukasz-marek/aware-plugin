package com.aware.plugin.template.data.transmission.impl;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.net.Uri;
import android.os.RemoteException;

/**
 * Created by lmarek on 20.02.2018.
 */

public class AsyncBufferedDataPersistingBus extends AsyncBufferedDataBus {

    private final ContentProviderClient contentProviderClient;

    protected AsyncBufferedDataPersistingBus(long bufferSize, ContentProviderClient providerClient) {
        super(bufferSize);
        this.contentProviderClient = providerClient;
    }

    @Override
    protected void processDataPart(Uri channelId, ContentValues[] data) {
        try {
            contentProviderClient.bulkInsert(channelId, data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
