package com.asd.vikrant.zypp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.app.DownloadManager.COLUMN_STATUS;

public class DatabaseHelper extends SQLiteOpenHelper
   {
    public static final String DB_NAME = "RiderDB";
    public static final String TRACKING_DATA_TABLE = "GPSDatatable";
    public static final String COLUMN_ID = "id";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String RIDE_NO = "ride";
    public static final String DIRECTION = "diraction";
    public static final String CURRENT_TIME = "currentTime";

    // create tracking data table ....................

    private String gps_dataSql = "CREATE TABLE " + TRACKING_DATA_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
               + LATITUDE + " VARCHAR, "
               + LONGITUDE + " VARCHAR, "
               + RIDE_NO + " VARCHAR, "
               + DIRECTION + " VARCHAR, "
               + CURRENT_TIME + " VARCHAR, "
               + COLUMN_STATUS + " TINYINT);";


    //database version ..........

    private static final int DB_VERSION = 1;
       public DatabaseHelper(Context context)
       {
           super(context, DB_NAME, null, DB_VERSION);
       }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(gps_dataSql);
        Log.d("query","value"+ gps_dataSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS TRACKING_DATA_TABLE";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

       public boolean addGpsData(String latitude, String longitude, String ride, String direction, String currentTime, int synce_status) {
           SQLiteDatabase db = this.getWritableDatabase();
           ContentValues contentValues = new ContentValues();
           contentValues.put(LATITUDE, latitude);
           contentValues.put(LONGITUDE, longitude);
           contentValues.put(RIDE_NO, ride);
           contentValues.put(DIRECTION, direction);
           contentValues.put(CURRENT_TIME, currentTime);
           contentValues.put(COLUMN_STATUS, synce_status);
           db.insert(TRACKING_DATA_TABLE, null, contentValues);
           db.close();
           return true;
       }

       // get db data.................
       public Cursor getData()
        {
           SQLiteDatabase db = this.getReadableDatabase();
           String sql = "SELECT * FROM " + TRACKING_DATA_TABLE + " ORDER BY " + COLUMN_ID + " ASC;";
           Cursor c = db.rawQuery(sql, null);
           return c;
       }

       //  get ride data.........
       public Cursor getUnSyncedData(String ride_no) {
           SQLiteDatabase db = this.getReadableDatabase();
           String sql = "SELECT * FROM " + TRACKING_DATA_TABLE + " WHERE " + RIDE_NO + " ='" + ride_no+"'";
           Cursor c = db.rawQuery(sql, null);
           return c;
       }
       // delete all table data this method ..............
       public void deleteAll()
       {
           SQLiteDatabase db = this.getWritableDatabase();
           db.execSQL("delete from "+ TRACKING_DATA_TABLE);
       }

       //get ride with distinct..............
       public Cursor getride()
       {
           SQLiteDatabase db = this.getReadableDatabase();
           String sql = "SELECT DISTINCT " + RIDE_NO + " FROM "+ TRACKING_DATA_TABLE;
           Cursor c = db.rawQuery(sql, null);
           return c;
       }
}
