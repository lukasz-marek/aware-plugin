package com.aware.plugin.template;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import com.aware.Aware;
import com.aware.plugin.template.communication.MessageRecipient;
import com.aware.plugin.template.communication.MessageSender;
import com.aware.plugin.template.communication.messages.ForceDisconnectMessage;
import com.aware.plugin.template.communication.messages.Message;

public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Plugin settings in XML @xml/preferences
    public static final String STATUS_PLUGIN_TEMPLATE = "status_plugin_template";

    //Plugin settings UI elements
    private static volatile CheckBoxPreference status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        final Preference button = findPreference("force_disconnect");
        button.setOnPreferenceClickListener(preference -> {
            MessageSender.sendMessageAsync(Plugin.RECIPIENT_NAME, new ForceDisconnectMessage());
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isStoragePermissionGranted();

    }

    private boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            status = (CheckBoxPreference) findPreference(STATUS_PLUGIN_TEMPLATE);
            if( Aware.getSetting(this, STATUS_PLUGIN_TEMPLATE).length() == 0 ) {
                Aware.setSetting( this, STATUS_PLUGIN_TEMPLATE, true ); //by default, the setting is true on install
            }
            status.setChecked(Aware.getSetting(getApplicationContext(), STATUS_PLUGIN_TEMPLATE).equals("true"));
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference setting = findPreference(key);
        if( setting.getKey().equals(STATUS_PLUGIN_TEMPLATE) ) {
            Aware.setSetting(this, key, sharedPreferences.getBoolean(key, false));
            if(null != sharedPreferences) {
                status.setChecked(sharedPreferences.getBoolean(key, false));
            }
        }
        if (Aware.getSetting(this, STATUS_PLUGIN_TEMPLATE).equals("true")) {
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.template");
        } else {
            Aware.stopPlugin(getApplicationContext(), "com.aware.plugin.template");
        }
    }
}
