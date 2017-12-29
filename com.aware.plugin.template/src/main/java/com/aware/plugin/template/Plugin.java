package com.aware.plugin.template;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.template.communication.MessageSender;
import com.aware.plugin.template.communication.MessageRecipient;
import com.aware.plugin.template.communication.messages.DeviceSelectedMessage;
import com.aware.plugin.template.communication.messages.Message;
import com.aware.plugin.template.sensor.listener.MetaWearSensorObserver;
import com.aware.plugin.template.sensor.listener.impl.AccelerometerObserver;
import com.aware.utils.Aware_Plugin;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.module.Led;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import bolts.Continuation;

public class Plugin extends Aware_Plugin implements MessageRecipient, ServiceConnection {

    public final static String RECIPIENT_NAME = Plugin.class.getName();

    private BtleService.LocalBinder serviceBinder;

    private final AtomicReference<MetaWearBoard> board = new AtomicReference<>();

    private final List<MetaWearSensorObserver> observers = new CopyOnWriteArrayList<>();

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        MessageSender.waitForMessages(this);
        getApplicationContext().bindService(new Intent(this, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        //This allows plugin data to be synced on demand from broadcast Aware#ACTION_AWARE_SYNC_DATA
        AUTHORITY = Provider.getAuthority(this);

        TAG = "AWARE::" + getResources().getString(R.string.app_name);

        /**
         * Plugins share their current status, i.e., context using this method.
         * This method is called automatically when triggering
         * {@link Aware#ACTION_AWARE_CURRENT_CONTEXT}
         **/
        CONTEXT_PRODUCER = new ContextProducer() {
            @Override
            public void onContext() {
                //Broadcast your context here
            }
        };

        //Add permissions you need (Android M+).
        //By default, AWARE asks access to the #Manifest.permission.WRITE_EXTERNAL_STORAGE

        //REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * Allow callback to other applications when data is stored in provider
     */
    private static AWARESensorObserver awareSensor;

    public static void setSensorObserver(AWARESensorObserver observer) {
        awareSensor = observer;
    }

    public static AWARESensorObserver getSensorObserver() {
        return awareSensor;
    }

    @Override
    public synchronized void receiveMessage(Message message) {
        switch (message.getMessageType()) {
            case DEVICE_SELECTED:
                final DeviceSelectedMessage deviceSelectedMessage = (DeviceSelectedMessage) message;
                final String deviceMacAddress = deviceSelectedMessage.getMacAddress();
                disconnectBoard();
                connectWithBoard(deviceMacAddress);

                break;
        }
    }

    @Override
    public String getRecipientName() {
        return RECIPIENT_NAME;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (BtleService.LocalBinder) iBinder;

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public interface AWARESensorObserver {
        void onDataChanged(ContentValues data);
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (PERMISSIONS_OK) {

            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

            //Initialize our plugin's settings
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);


            //Enable our plugin's sync-adapter to upload the data to the server if part of a study
            if (Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE).length() >= 0 && !Aware.isSyncEnabled(this, Provider.getAuthority(this)) && Aware.isStudy(this) && getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") || getApplicationContext().getResources().getBoolean(R.bool.standalone)) {
                ContentResolver.setIsSyncable(Aware.getAWAREAccount(this), Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Aware.getAWAREAccount(this), Provider.getAuthority(this), true);
                ContentResolver.addPeriodicSync(
                        Aware.getAWAREAccount(this),
                        Provider.getAuthority(this),
                        Bundle.EMPTY,
                        Long.parseLong(Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60
                );
            }

            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);


            if (!isBoardConnected()) {
                createDeviceSelectionNotification(getString(R.string.no_device_selected_notification_title), getString(R.string.no_device_selected_notification_content), NotificationIdentifier.NO_DEVICE_SELECTED.getIdentifier());
            }
        }

        return START_STICKY;
    }

    private void createDeviceSelectionNotification(String title, String content, int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);

        Intent resultIntent = new Intent(this, ChooseDeviceActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(id, mBuilder.build());
    }

    private synchronized void disconnectBoard(){
        if (isBoardConnected()) {
            try {
                observers.forEach(MetaWearSensorObserver::terminate);
                observers.clear();
                disableLed();
                board.get().disconnectAsync().waitForCompletion();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            board.set(null);
        }
    }

    private synchronized boolean isBoardConnected(){
        return board.get() != null && board.get().isConnected();
    }

    @Override
    public void onDestroy() {
        disconnectBoard();
        this.observers.clear();
        cancelAllNotifications();

        MessageSender.discardIncomingMessages(this);
        super.onDestroy();
        getApplicationContext().unbindService(this);


        //Turn off the sync-adapter if part of a study
        if (Aware.isStudy(this) && (getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") || getApplicationContext().getResources().getBoolean(R.bool.standalone))) {
            ContentResolver.setSyncAutomatically(Aware.getAWAREAccount(this), Provider.getAuthority(this), false);
            ContentResolver.removePeriodicSync(
                    Aware.getAWAREAccount(this),
                    Provider.getAuthority(this),
                    Bundle.EMPTY
            );
        }

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Stop AWARE instance in plugin
        Aware.stopAWARE(this);
    }

    private void connectWithBoard(String macAddress) {
        final BluetoothManager btManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (btManager != null && btManager.getAdapter() != null) {
            final BluetoothDevice remoteDevice =
                    btManager.getAdapter().getRemoteDevice(macAddress);

            board.set(serviceBinder.getMetaWearBoard(remoteDevice));
            board.get().connectAsync().continueWith((Continuation<Void, Void>) task -> {
                cancelAllNotifications();
                if (task.isFaulted()) {
                    createDeviceSelectionNotification(getString(R.string.connection_failed_notification_title), getString(R.string.connection_failed_notification_content), NotificationIdentifier.CONNECTION_TO_DEVICE_FAILED.getIdentifier());
                } else {
                    notifyUser(getString(R.string.connection_successful_notification_title), getString(R.string.connection_successful_notification_content), NotificationIdentifier.CONNECTION_TO_DEVICE_SUCCESSFUL.getIdentifier());
                }
                return null;
            }).onSuccess((Continuation<Void, Void>) task -> {
                initializeBoardListeners();
                return null;
            });
        }
    }

    private void initializeBoardListeners() {
        observers.add(new AccelerometerObserver(board.get(), this));
        /* add more listeners here*/

        enableLed();
    }

    private void enableLed(){
        final Led ledModule = board.get().getModule(Led.class);
        if (null != ledModule) {
            ledModule.play();
            ledModule.editPattern(Led.Color.GREEN, Led.PatternPreset.PULSE).commit();
        }
    }

    private void disableLed(){
        final Led ledModule = board.get().getModule(Led.class);
        if (null != ledModule) {
            ledModule.stop(true);
        }
    }

    private void notifyUser(String title, String content, int notificationId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content);


        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void cancelAllNotifications() {
        for (NotificationIdentifier identifier : NotificationIdentifier.values()) {
            notificationManager.cancel(identifier.getIdentifier());
        }
    }
}
