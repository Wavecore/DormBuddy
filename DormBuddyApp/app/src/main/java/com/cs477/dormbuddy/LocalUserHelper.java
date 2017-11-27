package com.cs477.dormbuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ndarwich on 11/17/17.
 */

//database helper that keeps user logged in and displays their templates
public class LocalUserHelper extends SQLiteOpenHelper {
    final static private Integer VERSION = 59;
    //===============Buildings Table=====================
    final static String TABLE_BUILDING = "dormbuddy_building";
    final static String BUILDING_ID = "building_id";
    final static String BUILDING_NAME = "building_name";
    //==============Building Maps Table==================
    final static String TABLE_BUILDING_MAPS = "dormbuddy_building_maps";
    final static String BUILDING_MAP_NAME = "building_map_name";
    final static String BUILDING_MAP = "building_map";
    //==============Room Table===========================
    final static String TABLE_ROOM = "dormbuddy_rooms";
    final static String ROOM_TYPE = "room_type";
    final static String ROOM_NUMBER = "room_number";
    final static String ROOM_MAX_OCCUPANCY = "room_max_occupancy";
    //==============Users Table==========================
    final static String TABLE_USER = "dormbuddy_user";
    final static String USER_ID = "user_id";
    final static String USER_NAME = "user_name";
    final static String USER_LOGGED_IN = "user_logged_in";
    final static String USER_ICON = "user_photo";
    //==============Reservation Table====================
    final static String TABLE_RESERVATION = "dormbuddy_reservations";
    final static String RESERVATION_TITLE = "reservation_title";
    final static String RESERVATION_DESCRIPTION = "reservation_description";
    final static String RESERVATION_START_TIME = "reservation_start_time";
    final static String RESERVATION_END_TIME = "reservation_end_time";
    final static String RESERVATION_ICON = "reservation_icon";

    final static String SELECTED_WASHER_TEMPLATE = "washer_template_selected";
    final static String SELECTED_DRYER_TEMPLATE = "dryer_template_selected";
    //user can only have a max of 5 templates
    final static String WASHER_TEMPLATE_1 = "washer_template_1";
    final static String DRYER_TEMPLATE_1 = "dryer_template_1";
    final static String WASHER_TEMPLATE_2 = "washer_template_2";
    final static String DRYER_TEMPLATE_2 = "dryer_template_2";
    final static String WASHER_TEMPLATE_3 = "washer_template_3";
    final static String DRYER_TEMPLATE_3 = "dryer_template_3";
    final static String WASHER_TEMPLATE_4 = "washer_template_4";
    final static String DRYER_TEMPLATE_4 = "dryer_template_4";
    final static String WASHER_TEMPLATE_5 = "washer_template_5";
    final static String DRYER_TEMPLATE_5 = "dryer_template_5";









    final private String[] TABLES = {TABLE_BUILDING,TABLE_BUILDING_MAPS,TABLE_ROOM,TABLE_USER,TABLE_RESERVATION};

    final Context context;
    /*
     * STORES THE FOLLOWING LOCALLY:
     * - G number
     * - Full name
     * - is the user logged in? (to avoid credentials)
     * - building id
     * - building name
     * - room number
     * - user profile buddy icon
     * - selected washer and dryer template
     * - all 5 washer and dryer templates
     */
    final private String CREATE_USER =
            "CREATE TABLE "+TABLE_USER+" (" +
                    USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_LOGGED_IN + " INTEGER DEFAULT 0, " +
                    BUILDING_ID + " INTEGER DEFAULT 0, " +
                    BUILDING_NAME + " TEXT NOT NULL, " +
                    ROOM_NUMBER + " TEXT NOT NULL, " +
                    USER_ICON + " BLOB, " +
                    SELECTED_WASHER_TEMPLATE + " INTEGER DEFAULT 0, " +
                    SELECTED_DRYER_TEMPLATE + " INTEGER DEFAULT 0, " +
                    WASHER_TEMPLATE_1 + " TEXT NOT NULL DEFAULT '', " +
                    DRYER_TEMPLATE_1 + " TEXT NOT NULL DEFAULT '', " +
                    WASHER_TEMPLATE_2 + " TEXT NOT NULL DEFAULT '', " +
                    DRYER_TEMPLATE_2 + " TEXT NOT NULL DEFAULT '', " +
                    WASHER_TEMPLATE_3 + " TEXT NOT NULL DEFAULT '', " +
                    DRYER_TEMPLATE_3 + " TEXT NOT NULL DEFAULT '', " +
                    WASHER_TEMPLATE_4 + " TEXT NOT NULL DEFAULT '', " +
                    DRYER_TEMPLATE_4 + " TEXT NOT NULL DEFAULT '', " +
                    WASHER_TEMPLATE_5 + " TEXT NOT NULL DEFAULT '', " +
                    DRYER_TEMPLATE_5 + " TEXT NOT NULL DEFAULT '')";
    final private String CREATE_BUILDING =
            "CREATE TABLE "+TABLE_BUILDING+" ("+
                    BUILDING_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    BUILDING_NAME + " TEXT NOT NULL);";
    final private String CREATE_BUILDING_MAPS =
            "CREATE TABLE "+TABLE_BUILDING_MAPS+" ("+
                    BUILDING_ID +" INTEGER NOT NULL, "+
                    BUILDING_MAP_NAME+" INTEGER NOT NULL, "+
                    BUILDING_MAP + " BLOB NOT NULL, "+
                    "PRIMARY KEY ("+BUILDING_ID+", "+BUILDING_MAP_NAME+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_BUILDING+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private String CREATE_ROOM =
            "CREATE TABLE "+TABLE_ROOM+" ("+
                    BUILDING_ID +" INTEGER, "+
                    ROOM_NUMBER+" INTEGER NOT NULL, "+
                    ROOM_TYPE+" TEXT CHECK("+ROOM_TYPE+" IN ('LAUNDRY','DORM','STUDY','EVENT')),"+
                    ROOM_MAX_OCCUPANCY+" INTEGER CHECK("+ROOM_MAX_OCCUPANCY+" >= 0),"+
                    "PRIMARY KEY ("+BUILDING_ID+", "+ROOM_NUMBER+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_BUILDING+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private  String CREATE_RESERVATION =
            "CREATE TABLE "+TABLE_RESERVATION+" ("+
                    BUILDING_ID +" INTEGER, "+
                    ROOM_NUMBER+" INTEGER NOT NULL, "+
                    USER_ID+ " INTEGER, "+
                    RESERVATION_TITLE+ " TEXT, "+
                    RESERVATION_DESCRIPTION+" TEXT, "+
                    RESERVATION_ICON+ " BLOB, "+
                    RESERVATION_START_TIME+ " INTEGER NOT NULL, "+
                    RESERVATION_END_TIME+ " INTEGER NOT NULL, "+
                    "PRIMARY KEY ("+BUILDING_ID+", "+ROOM_NUMBER+","+RESERVATION_START_TIME+","+RESERVATION_END_TIME+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_ROOM+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE,"+
                    "FOREIGN KEY ("+ROOM_NUMBER+") REFERENCES "+TABLE_ROOM+" ("+ROOM_NUMBER+") ON DELETE SET NULL DEFERRABLE,"+
                    "FOREIGN KEY ("+USER_ID+") REFERENCES "+TABLE_USER+" ("+USER_ID+") ON DELETE SET NULL DEFERRABLE);";


    final private String[] CREATE_TABLES = {CREATE_BUILDING,CREATE_BUILDING_MAPS,CREATE_ROOM,CREATE_USER,CREATE_RESERVATION};

    public LocalUserHelper(Context context) {
        super(context, TABLE_USER, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(int x = 0; x < CREATE_TABLES.length;x++)
            db.execSQL(CREATE_TABLES[x]);
       // db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println("TABLE NAME=> "+c.getString(0));
                c.moveToNext();
            }
        }
        for(int x = TABLES.length-1; x >= 0; x --) {
            System.out.println("DROP TABLE IF EXISTS "+TABLES[x]);
            db.execSQL("DROP TABLE IF EXISTS " + TABLES[x]);
        }
        System.out.println("AFTER DROPS");
        c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println("TABLE NAME=> "+c.getString(0));
                c.moveToNext();
            }
        }
      //  db.execSQL("DROP TABLE IF EXISTS dormbuddy_user");
      //  db.execSQL("DROP TABLE IF EXISTS "+TABLE_BUILDING);
      //  db.execSQL("DROP TABLE IF EXISTS "+TABLE_BUILDING_MAPS);
        onCreate(db);
    }

    void deleteDatabase ( ) {
        context.deleteDatabase(TABLE_USER);
    }
}
