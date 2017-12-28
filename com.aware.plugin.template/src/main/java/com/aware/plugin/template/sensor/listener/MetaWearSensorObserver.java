package com.aware.plugin.template.sensor.listener;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;

import com.aware.plugin.template.Provider;
import com.mbientlab.metawear.MetaWearBoard;

/**
 * Created by lmarek on 27.12.2017.
 */

public abstract class MetaWearSensorObserver {

    private final Context observerContext;

    protected final ContentProviderClient providerClient;

    public MetaWearSensorObserver(MetaWearBoard metaWearBoard, Context context){
        observerContext = context;
        final ContentResolver contentResolver = observerContext.getContentResolver();

        providerClient = contentResolver.acquireContentProviderClient(Provider.AUTHORITY);

        registerObserver(metaWearBoard);
    }

    protected abstract void registerObserver(MetaWearBoard metaWearBoard);
}
