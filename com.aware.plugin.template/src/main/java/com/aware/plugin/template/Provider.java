package com.aware.plugin.template;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aware.Aware;
import com.aware.utils.DatabaseHelper;

import java.util.HashMap;

public class Provider extends ContentProvider {

    public static String AUTHORITY = "com.aware.plugin.template.provider.mbientlab"; //change to package.provider.your_plugin_name

    public static final int DATABASE_VERSION = 6; //increase this if you make changes to the database structure, i.e., rename columns, etc.
    public static final String DATABASE_NAME = "plugin_mbientlab.db"; //the database filename, use plugin_xxx for plugins.

    //Add here your database table names, as many as you need
    public static final String DB_ACCELERATION = "acceleration";
    public static final String DB_VELOCITY = "velocity";
    public static final String DB_MAGNETIC = "magnetic";
    public static final String DB_TEMPERATURE = "temperature";
    public static final String DB_PRESSURE = "pressure";




    //For each table, add two indexes: DIR and ITEM. The index needs to always increment. Next one is 3, and so on.
    private static final int ACCELERATION_DIR = 1;
    private static final int ACCELERATION_ITEM = 2;

    private static final int VELOCITY_DIR = 3;
    private static final int VELOCITY_ITEM = 4;

    private static final int MAGNETIC_DIR = 5;
    private static final int MAGNETIC_ITEM = 6;

    private static final int TEMPERATURE_DIR = 7;
    private static final int TEMPERATURE_ITEM = 8;


    private static final int PRESSURE_DIR = 9;
    private static final int PRESSURE_ITEM = 10;

    //Put tables names in this array so AWARE knows what you have on the database
    public static final String[] DATABASE_TABLES = {
            DB_ACCELERATION,
            DB_VELOCITY,
            DB_MAGNETIC,
            DB_TEMPERATURE,
            DB_PRESSURE
    };

    //These are columns that we need to sync data, don't change this!
    public interface AWAREColumns extends BaseColumns {
        String _ID = "_id";
        String TIMESTAMP = "timestamp";
        String DEVICE_ID = "device_id";
    }

    /**
     * Create one of these per database table
     * In this example, we are adding example columns
     */
    public static final class Acceleration_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_ACCELERATION);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.acceleration"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.acceleration"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String X = "double_x"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Y = "double_y"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Z = "double_z"; //a double_ prefix makes a MySQL DOUBLE column

    }

    public static final class Velocity_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_VELOCITY);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.velocity"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.velocity"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String X = "double_x"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Y = "double_y"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Z = "double_z"; //a double_ prefix makes a MySQL DOUBLE column

    }

    public static final class Magnetic_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_MAGNETIC);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.magnetic"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.magnetic"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String X = "double_x"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Y = "double_y"; //a double_ prefix makes a MySQL DOUBLE column
        public static final String Z = "double_z"; //a double_ prefix makes a MySQL DOUBLE column

    }

    public static final class Temperature_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TEMPERATURE);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.temperature"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.temperature"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String TEMPERATURE_IN_CELSIUS = "double_temperature"; //a double_ prefix makes a MySQL DOUBLE column

    }

    public static final class Pressure_Data implements AWAREColumns {
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_PRESSURE);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.pressure"; //modify me
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.aware.plugin.template.provider.pressure"; //modify me

        //Note: integers and strings don't need a type prefix_
        public static final String PRESSURE_IN_PASCALS = "double_pressure"; //a double_ prefix makes a MySQL DOUBLE column

    }

    //Define each database table fields
    private static final String DB_ACCELERATION_FIELDS =
            Acceleration_Data._ID + " integer primary key autoincrement," +
                    Acceleration_Data.TIMESTAMP + " real default 0," +
                    Acceleration_Data.DEVICE_ID + " text default ''," +
                    Acceleration_Data.X + " real default 0," +
                    Acceleration_Data.Y + " real default 0," +
                    Acceleration_Data.Z + " real default 0";

    private static final String DB_VELOCITY_FIELDS =
            Velocity_Data._ID + " integer primary key autoincrement," +
                    Velocity_Data.TIMESTAMP + " real default 0," +
                    Velocity_Data.DEVICE_ID + " text default ''," +
                    Velocity_Data.X + " real default 0," +
                    Velocity_Data.Y + " real default 0," +
                    Velocity_Data.Z + " real default 0";

    private static final String DB_MAGNETIC_FIELDS =
            Magnetic_Data._ID + " integer primary key autoincrement," +
                    Magnetic_Data.TIMESTAMP + " real default 0," +
                    Magnetic_Data.DEVICE_ID + " text default ''," +
                    Magnetic_Data.X + " real default 0," +
                    Magnetic_Data.Y + " real default 0," +
                    Magnetic_Data.Z + " real default 0";

    private static final String DB_TEMPERATURE_FIELDS =
            Temperature_Data._ID + " integer primary key autoincrement," +
                    Temperature_Data.TIMESTAMP + " real default 0," +
                    Temperature_Data.DEVICE_ID + " text default ''," +
                    Temperature_Data.TEMPERATURE_IN_CELSIUS + " real default 0";

    private static final String DB_PRESSURE_FIELDS =
            Pressure_Data._ID + " integer primary key autoincrement," +
                    Pressure_Data.TIMESTAMP + " real default 0," +
                    Pressure_Data.DEVICE_ID + " text default ''," +
                    Pressure_Data.PRESSURE_IN_PASCALS + " real default 0";

    /**
     * Share the fields with AWARE so we can replicate the table schema on the server
     */
    public static final String[] TABLES_FIELDS = {
            DB_ACCELERATION_FIELDS,
            DB_VELOCITY_FIELDS,
            DB_MAGNETIC_FIELDS,
            DB_TEMPERATURE_FIELDS,
            DB_PRESSURE_FIELDS
    };

    //Helper variables for ContentProvider - DO NOT CHANGE
    private UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    private void initialiseDatabase() {
        if (dbHelper == null)
            dbHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION, DATABASE_TABLES, TABLES_FIELDS);
        if (database == null)
            database = dbHelper.getWritableDatabase();
    }
    //--

    //For each table, create a hashmap needed for database queries
    private HashMap<String, String> accelerationMap;

    private HashMap<String, String> velocityMap;

    private HashMap<String, String> magneticMap;

    private HashMap<String, String> temperatureMap;

    private HashMap<String, String> pressureMap;



    /**
     * Returns the provider authority that is dynamic
     *
     * @return
     */
    public static String getAuthority(Context context) {
        AUTHORITY = context.getPackageName() + ".provider.mbientlab";
        return AUTHORITY;
    }

    @Override
    public boolean onCreate() {
        //This is a hack to allow providers to be reusable in any application/plugin by making the authority dynamic using the package name of the parent app
        AUTHORITY = getAuthority(getContext());

        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //For each table, add indexes DIR and ITEM
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0], ACCELERATION_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[0] + "/#", ACCELERATION_ITEM);

        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1], VELOCITY_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[1] + "/#", VELOCITY_ITEM);

        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2], MAGNETIC_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[2] + "/#", MAGNETIC_ITEM);

        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[3], TEMPERATURE_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[3] + "/#", TEMPERATURE_ITEM);

        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[4], PRESSURE_DIR);
        sUriMatcher.addURI(AUTHORITY, DATABASE_TABLES[4] + "/#", PRESSURE_ITEM);

        //Create each table hashmap so Android knows how to insert data to the database. Put ALL table fields.
        accelerationMap = new HashMap<>();
        accelerationMap.put(Acceleration_Data._ID, Acceleration_Data._ID);
        accelerationMap.put(Acceleration_Data.TIMESTAMP, Acceleration_Data.TIMESTAMP);
        accelerationMap.put(Acceleration_Data.DEVICE_ID, Acceleration_Data.DEVICE_ID);
        accelerationMap.put(Acceleration_Data.X, Acceleration_Data.X);
        accelerationMap.put(Acceleration_Data.Y, Acceleration_Data.Y);
        accelerationMap.put(Acceleration_Data.Z, Acceleration_Data.Z);

        velocityMap = new HashMap<>();
        velocityMap.put(Velocity_Data._ID, Velocity_Data._ID);
        velocityMap.put(Velocity_Data.TIMESTAMP, Velocity_Data.TIMESTAMP);
        velocityMap.put(Velocity_Data.DEVICE_ID, Velocity_Data.DEVICE_ID);
        velocityMap.put(Velocity_Data.X, Velocity_Data.X);
        velocityMap.put(Velocity_Data.Y, Velocity_Data.Y);
        velocityMap.put(Velocity_Data.Z, Velocity_Data.Z);

        magneticMap = new HashMap<>();
        magneticMap.put(Magnetic_Data._ID, Magnetic_Data._ID);
        magneticMap.put(Magnetic_Data.TIMESTAMP, Magnetic_Data.TIMESTAMP);
        magneticMap.put(Magnetic_Data.DEVICE_ID, Magnetic_Data.DEVICE_ID);
        magneticMap.put(Magnetic_Data.X, Magnetic_Data.X);
        magneticMap.put(Magnetic_Data.Y, Magnetic_Data.Y);
        magneticMap.put(Magnetic_Data.Z, Magnetic_Data.Z);

        temperatureMap = new HashMap<>();
        temperatureMap.put(Temperature_Data._ID, Temperature_Data._ID);
        temperatureMap.put(Temperature_Data.TIMESTAMP, Temperature_Data.TIMESTAMP);
        temperatureMap.put(Temperature_Data.DEVICE_ID, Temperature_Data.DEVICE_ID);
        temperatureMap.put(Temperature_Data.TEMPERATURE_IN_CELSIUS, Temperature_Data.TEMPERATURE_IN_CELSIUS);

        pressureMap = new HashMap<>();
        pressureMap.put(Pressure_Data._ID, Pressure_Data._ID);
        pressureMap.put(Pressure_Data.TIMESTAMP, Pressure_Data.TIMESTAMP);
        pressureMap.put(Pressure_Data.DEVICE_ID, Pressure_Data.DEVICE_ID);
        pressureMap.put(Pressure_Data.PRESSURE_IN_PASCALS, Pressure_Data.PRESSURE_IN_PASCALS);

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        initialiseDatabase();

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {

            //Add each table DIR case, increasing the index accordingly
            case ACCELERATION_DIR:
                count = database.delete(DATABASE_TABLES[0], selection, selectionArgs);
                break;
            case VELOCITY_DIR:
                count = database.delete(DATABASE_TABLES[1], selection, selectionArgs);
                break;
            case MAGNETIC_DIR:
                count = database.delete(DATABASE_TABLES[2], selection, selectionArgs);
                break;
            case TEMPERATURE_DIR:
                count = database.delete(DATABASE_TABLES[3], selection, selectionArgs);
                break;
            case PRESSURE_DIR:
                count = database.delete(DATABASE_TABLES[4], selection, selectionArgs);
                break;
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        database.setTransactionSuccessful();
        database.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {

        initialiseDatabase();

        ContentValues values = (initialValues != null) ? new ContentValues(initialValues) : new ContentValues();

        database.beginTransaction();
        long _id;
        switch (sUriMatcher.match(uri)) {
            //Add each table DIR case
            case ACCELERATION_DIR:
                _id = database.insert(DATABASE_TABLES[0], Acceleration_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Acceleration_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            case VELOCITY_DIR:
                _id = database.insert(DATABASE_TABLES[1], Velocity_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Velocity_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            case MAGNETIC_DIR:
                _id = database.insert(DATABASE_TABLES[2], Magnetic_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Magnetic_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            case TEMPERATURE_DIR:
                _id = database.insert(DATABASE_TABLES[3], Temperature_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Temperature_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            case PRESSURE_DIR:
                _id = database.insert(DATABASE_TABLES[4], Pressure_Data.DEVICE_ID, values);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (_id > 0) {
                    Uri dataUri = ContentUris.withAppendedId(Pressure_Data.CONTENT_URI, _id);
                    getContext().getContentResolver().notifyChange(dataUri, null);
                    return dataUri;
                }
                database.endTransaction();
                throw new SQLException("Failed to insert row into " + uri);
            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        initialiseDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {

            //Add all tables' DIR entries, with the right table index
            case ACCELERATION_DIR:
                qb.setTables(DATABASE_TABLES[0]);
                qb.setProjectionMap(accelerationMap); //the hashmap of the table
                break;
            case VELOCITY_DIR:
                qb.setTables(DATABASE_TABLES[1]);
                qb.setProjectionMap(velocityMap); //the hashmap of the table
                break;
            case MAGNETIC_DIR:
                qb.setTables(DATABASE_TABLES[2]);
                qb.setProjectionMap(magneticMap); //the hashmap of the table
                break;
            case TEMPERATURE_DIR:
                qb.setTables(DATABASE_TABLES[3]);
                qb.setProjectionMap(temperatureMap); //the hashmap of the table
                break;
            case PRESSURE_DIR:
                qb.setTables(DATABASE_TABLES[4]);
                qb.setProjectionMap(pressureMap); //the hashmap of the table
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //Don't change me
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {
            if (Aware.DEBUG)
                Log.e(Aware.TAG, e.getMessage());
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {

            //Add each table indexes DIR and ITEM
            case ACCELERATION_DIR:
                return Acceleration_Data.CONTENT_TYPE;
            case ACCELERATION_ITEM:
                return Acceleration_Data.CONTENT_ITEM_TYPE;
            case VELOCITY_DIR:
                return Velocity_Data.CONTENT_TYPE;
            case VELOCITY_ITEM:
                return Velocity_Data.CONTENT_ITEM_TYPE;
            case MAGNETIC_DIR:
                return Magnetic_Data.CONTENT_TYPE;
            case MAGNETIC_ITEM:
                return Magnetic_Data.CONTENT_ITEM_TYPE;
            case TEMPERATURE_DIR:
                return Temperature_Data.CONTENT_TYPE;
            case TEMPERATURE_ITEM:
                return Temperature_Data.CONTENT_ITEM_TYPE;
            case PRESSURE_DIR:
                return Pressure_Data.CONTENT_TYPE;
            case PRESSURE_ITEM:
                return Pressure_Data.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        initialiseDatabase();

        database.beginTransaction();

        int count;
        switch (sUriMatcher.match(uri)) {

            //Add each table DIR case
            case ACCELERATION_DIR:
                count = database.update(DATABASE_TABLES[0], values, selection, selectionArgs);
                break;

            case VELOCITY_DIR:
                count = database.update(DATABASE_TABLES[1], values, selection, selectionArgs);
                break;

            case MAGNETIC_DIR:
                count = database.update(DATABASE_TABLES[2], values, selection, selectionArgs);
                break;

            case TEMPERATURE_DIR:
                count = database.update(DATABASE_TABLES[3], values, selection, selectionArgs);
                break;

            case PRESSURE_DIR:
                count = database.update(DATABASE_TABLES[4], values, selection, selectionArgs);
                break;

            default:
                database.endTransaction();
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        database.setTransactionSuccessful();
        database.endTransaction();

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
