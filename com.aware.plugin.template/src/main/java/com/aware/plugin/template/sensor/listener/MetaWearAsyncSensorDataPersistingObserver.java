package com.aware.plugin.template.sensor.listener;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;

import com.aware.plugin.template.Provider;
import com.aware.plugin.template.data.transmission.BusConnection;
import com.aware.plugin.template.data.transmission.DataBus;
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

    private final List<Runnable> tasksForTermination = new CopyOnWriteArrayList<>();

    private final AtomicBoolean isTerminating = new AtomicBoolean();

    protected final static short DATA_READ_LIMIT_IN_MILLISECONDS = 200; // 1 sample per 200 ms => 1/0.2s == 5Hz

    protected final static float DATA_PRODUCTION_FREQUENCY = 5f; // 5Hz

    private final BusConnection busConnection;


    public MetaWearAsyncSensorDataPersistingObserver(DataBus dataBus) {
        isTerminating.set(false);

        busConnection = dataBus.registerChannel(getDatabaseContentUri());

    }

    /**
     * This method should be included in {registerObserver} in a way that allows it to be called every time new data arrives from the board
     *
     * @param sensorReadings data acquired from board
     */
    protected final void processData(Data sensorReadings) {
        if(!isTerminating.get()) {

            final ContentValues readingsToSave = convertToDatabaseRecord(sensorReadings);
            busConnection.transmit(readingsToSave);

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
        busConnection.terminate();
    }

    protected final void addTerminationTask(Runnable task) {
        tasksForTermination.add(task);
    }

    protected abstract Uri getDatabaseContentUri();
}
