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
    final static private Integer VERSION = 65;
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
    final static String ROOM_TYPE_DORM = "DORM";
    final static String ROOM_TYPE_LAUNDRY = "LAUNDRY";
    final static String ROOM_TYPE_EVENT = "EVENT";
    final static String ROOM_TYPE_STUDY = "STUDY";
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
    final static String RESERVATION_IS_EVENT = "reservation_is_event";

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
                    ROOM_TYPE+" TEXT CHECK("+ROOM_TYPE+" IN ('"+ROOM_TYPE_LAUNDRY+"','"+ROOM_TYPE_DORM+"','"+ROOM_TYPE_STUDY+"','"+ROOM_TYPE_EVENT+"')),"+
                    ROOM_MAX_OCCUPANCY+" INTEGER CHECK("+ROOM_MAX_OCCUPANCY+" >= 0),"+
                    "PRIMARY KEY ("+BUILDING_ID+", "+ROOM_NUMBER+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_BUILDING+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private  String CREATE_RESERVATION =
            "CREATE TABLE "+TABLE_RESERVATION+" ("+
                    BUILDING_ID +" INTEGER, "+
                    ROOM_NUMBER+" TEXT NOT NULL, "+
                    USER_ID+ " INTEGER, "+
                    RESERVATION_TITLE+ " TEXT, "+
                    RESERVATION_DESCRIPTION+" TEXT, "+
                    RESERVATION_ICON+ " BLOB, "+
                    RESERVATION_IS_EVENT+" BOOLEAN, "+
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
        addInitialEntries(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int x = TABLES.length-1; x >= 0; x --)
            db.execSQL("DROP TABLE IF EXISTS " + TABLES[x]);
        onCreate(db);
    }
    private void addInitialEntries(SQLiteDatabase db){
        // Insert Buildings
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES( 0, 'Fake Building');");    //ID 0
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_NAME+") VALUES('Beacon Hall');");    //ID 1
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_NAME+") VALUES('Blue Ridge');");     //ID 2
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_NAME+") VALUES('The Commons');");    //ID 3
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_NAME+") VALUES('Commonwealth');");   //ID 4
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_NAME+") VALUES('Dominion');");       //ID 5
        Cursor c = db.rawQuery("SELECT "+BUILDING_ID+" FROM "+TABLE_BUILDING, null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println(c.getString(0));
                c.moveToNext();
            }
        }
        // Insert Rooms
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 0,'369a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 1,'222','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 1,'223','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 1,'108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 1,'123','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 1,'333','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'222','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'333','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'444','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'555','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'111','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'666','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 2,'777','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'444','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'69','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'213','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'777','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'666','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'453','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 3,'333','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'444a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'444b','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'213','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'777','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'666','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'453','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 4,'333','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'444a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'444b','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'213','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'777','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'666','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'453','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 5,'333','LAUNDRY' );");
        // Insert Fake User
      //  db.execSQL("INSERT INTO "+TABLE_USER+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+USER_ID+", "+USER_NAME+", "+BUILDING_NAME+")"+
      //          " VALUES( 0,'369a',666, 'Anonymous','Fake Building');");
        // Insert Reservations

    }
/*
INSERT INTO Location(RoomType,BCode,RoomNum,NumSeats,Area) VALUES('Office','ENG',520,0,224);
 USER_ID + " INTEGER PRIMARY KEY, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_LOGGED_IN + " INTEGER DEFAULT 0, " +
                    BUILDING_ID + " INTEGER DEFAULT 0, " +
                    BUILDING_NAME + " TEXT NOT NULL, " +
                    ROOM_NUMBER + " TEXT NOT NULL, " +
                    USER_ICON + " BLOB, " +
 */
}
