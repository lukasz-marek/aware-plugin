package com.aware.plugin.template.data.transmission.impl;

import android.content.ContentValues;
import android.net.Uri;

import com.aware.plugin.template.data.transmission.BusConnection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lmarek on 20.02.2018.
 */

public abstract class AsyncBufferedDataBus extends BufferedDataBus {

    private final ExecutorService worker = Executors.newSingleThreadExecutor();

    /**
     * Proxy that makes transmit method async.
     */

    protected class AsyncBusConnection implements BusConnection {

        private final ExecutorService worker = Executors.newSingleThreadExecutor();

        private final BusConnection connection;

        protected AsyncBusConnection(BusConnection connection) {
            this.connection = connection;
        }

        @Override
        public void transmit(ContentValues data) {
            worker.submit(() -> connection.transmit(data));
        }

        @Override
        public void terminate() {
            connection.terminate();
            worker.shutdown();
        }
    }

    protected AsyncBufferedDataBus(long bufferSize) {
        super(bufferSize);
    }

    public BusConnection registerChannel(Uri channel) {
        final BusConnection defaultConnection =  super.registerChannel(channel);
        return new AsyncBusConnection(defaultConnection);
    }

    @Override
    protected final void processFlushedData(Map<Uri, List<ContentValues>> bufferCopy) {
        for (Uri key : bufferCopy.keySet()) {
            final List<ContentValues> data = bufferCopy.get(key);
            final ContentValues dataArray[] = (ContentValues[]) data.toArray();
            worker.submit(() -> processDataPart(key, dataArray));
        }
    }

    protected abstract void processDataPart(Uri channelId, ContentValues data[]);

    @Override
    public void terminate() {
        super.terminate();
        worker.shutdown();
    }
}
