package com.aware.plugin.template.sensor.listener;

import android.content.ContentValues;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SensorDataForwarder {

    private final ConcurrentHashMap<String, Consumer<ContentValues>> tasks = new ConcurrentHashMap<>();

    public void registerForwardingTask(String sourceId, Consumer<ContentValues> task){
        tasks.put(sourceId, task);
    }

    public void deleteForwardingTask(String sourceId){
        tasks.remove(sourceId);
    }

    void forward(String sourceId, ContentValues data){
        final Consumer<ContentValues> task = tasks.get(sourceId);
        if(task != null){
            task.accept(data);
        }
    }
}
