package com.aware.plugin.template.sensor.listener;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.aware.plugin.template.Provider;
import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

/**
 * Created by lmarek on 27.12.2017.
 */

public abstract class MetaWearAsyncSensorPersistingObserver implements MetaWearSensorObserver{

    private final ContentProviderClient providerClient;

    private final List<Supplier<Void>> tasksForTermination = new CopyOnWriteArrayList<>();


    public MetaWearAsyncSensorPersistingObserver(Context context){
        final Context observerContext = context;
        final ContentResolver contentResolver = observerContext.getContentResolver();

        providerClient = contentResolver.acquireContentProviderClient(Provider.AUTHORITY);
    }

    /**
     * This method should be included in {registerObserver} in a way that allows it to be called every time new data arrives from the board
     * @param sensorReadings data acquired from board
     */
    protected final void processData(Data sensorReadings){
        final ContentValues readingsToSave = convertToDatabaseRecord(sensorReadings);
        saveSensorReadingsToDatabase(readingsToSave);
    }

    protected abstract ContentValues convertToDatabaseRecord(Data data);

    public abstract void register(MetaWearBoard metaWearBoard);

    public final void terminate(){
        tasksForTermination.forEach(Supplier::get);
        providerClient.close();
    }

    protected final void addTerminationTask(Supplier<Void> task){
        tasksForTermination.add(task);

    }

    private void saveSensorReadingsToDatabase(ContentValues readings){
        try {
            providerClient.insert(getDatabaseContentUri(), readings);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected abstract Uri getDatabaseContentUri();
}
