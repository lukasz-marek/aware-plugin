package com.aware.plugin.template.data.transmission.impl;

import android.content.ContentValues;
import android.net.Uri;

import com.aware.plugin.template.data.transmission.BusConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lmarek on 20.02.2018.
 */

public abstract class BufferedDataBus extends DataBusTemplate {

    private final ConcurrentHashMap<Uri, List<ContentValues>> buffer = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Uri, List<ContentValues>> backupBuffer = new ConcurrentHashMap<>();

    private final Set<Uri> terminatedChannels = new CopyOnWriteArraySet<>();

    private final AtomicBoolean isFlushing = new AtomicBoolean(false);

    private final AtomicBoolean isTerminating = new AtomicBoolean(false);

    private final AtomicLong bufferedElementsCount = new AtomicLong(0);

    private final long bufferSize;

    @Override
    public BusConnection registerChannel(Uri channelId) {
        buffer.putIfAbsent(channelId, new CopyOnWriteArrayList<>());
        backupBuffer.putIfAbsent(channelId, new CopyOnWriteArrayList<>());
        terminatedChannels.remove(channelId);

        return super.registerChannel(channelId);
    }

    @Override
    public void terminate() {
        isTerminating.set(true);
        flushBuffer();
    }

    protected BufferedDataBus(long bufferSize) {
        this.bufferSize = bufferSize;
    }


    protected final void handleTransmission(Uri channel, ContentValues data) {

        if (isTerminating.get() || terminatedChannels.contains(channel)) {
            return;
        }

        if (isFlushing.get()) {
            synchronized (backupBuffer) {
                if (isFlushing.get()) {
                    backupBuffer.get(channel).add(data);
                    return;
                }
            }
        }

        buffer.get(channel).add(data);
        bufferedElementsCount.incrementAndGet();

        if (bufferedElementsCount.get() >= bufferSize) {
            synchronized (this) {
                if (bufferedElementsCount.get() >= bufferSize) {
                    flushBuffer();
                }
            }
        }
    }

    private synchronized void flushBuffer() {
        try {
            flushBufferInternal();
        } catch (Exception e) {
            isFlushing.set(false);
            e.printStackTrace();
        }
    }

    private void flushBufferInternal() {
        isFlushing.set(true);
        if (bufferedElementsCount.get() >= bufferSize) {
            final Map<Uri, List<ContentValues>> dataToStore = new HashMap<>(buffer);
            buffer.values().forEach(List::clear);
            processFlushedData(dataToStore);
        }

        synchronized (backupBuffer) {
            bufferedElementsCount.set(0);

            for (Uri key : backupBuffer.keySet()) {
                final List<ContentValues> awaitingValues = new CopyOnWriteArrayList<>(backupBuffer.get(key));
                bufferedElementsCount.addAndGet(awaitingValues.size());
                buffer.putIfAbsent(key, awaitingValues);
            }

            backupBuffer.values().forEach(List::clear);
            isFlushing.set(false);
        }
    }

    protected abstract void processFlushedData(Map<Uri, List<ContentValues>> bufferCopy);

    protected void terminateChannel(Uri channel) {
        terminatedChannels.add(channel);
        flushBuffer();
    }
}
