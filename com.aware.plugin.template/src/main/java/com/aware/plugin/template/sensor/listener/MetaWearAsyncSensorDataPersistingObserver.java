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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by lmarek on 27.12.2017.
 */

public abstract class MetaWearAsyncSensorDataPersistingObserver implements MetaWearSensorObserver {

    private final ContentProviderClient providerClient;

    private final List<Runnable> tasksForTermination = new CopyOnWriteArrayList<>();

    private final AtomicBoolean isTerminating = new AtomicBoolean();

    protected final static short DATA_READ_LIMIT_IN_MILLISECONDS = 200; // 1 sample per 200 ms => 1/0.2s == 5Hz

    protected final static float DATA_PRODUCTION_FREQUENCY = 5f; // 5Hz


    public MetaWearAsyncSensorDataPersistingObserver(Context context) {
        isTerminating.set(false);
        final ContentResolver contentResolver = context.getContentResolver();

        providerClient = contentResolver.acquireContentProviderClient(Provider.AUTHORITY);
    }

    /**
     * This method should be included in {registerObserver} in a way that allows it to be called every time new data arrives from the board
     *
     * @param sensorReadings data acquired from board
     */
    protected final void processData(Data sensorReadings) {
        if(!isTerminating.get()) {

            final ContentValues readingsToSave = convertToDatabaseRecord(sensorReadings);
            saveSensorReadingsToDatabase(readingsToSave);

        }
    }

    private ContentValues convertToDatabaseRecord(Data data){
        final Calendar timestamp = data.timestamp();

        final ContentValues contentValues = new ContentValues();
        final Timestamp sqlTimestamp = new Timestamp(timestamp.getTime().getTime());
        contentValues.put(Provider.AWAREColumns.TIMESTAMP, sqlTimestamp.toString());

        fillWithSensorSpecificData(contentValues, data);
        return contentValues;
    }

    protected abstract void fillWithSensorSpecificData(ContentValues contentValues, Data data);

    public abstract void register(MetaWearBoard metaWearBoard);

    public final void terminate() {
        isTerminating.set(true);
        tasksForTermination.forEach(Runnable::run);
        providerClient.close();
    }

    protected final void addTerminationTask(Runnable task) {
        tasksForTermination.add(task);

    }

    private void saveSensorReadingsToDatabase(ContentValues readings) {
        try {
            providerClient.insert(getDatabaseContentUri(), readings);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected abstract Uri getDatabaseContentUri();
}
