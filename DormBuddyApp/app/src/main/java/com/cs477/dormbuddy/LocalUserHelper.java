package com.cs477.dormbuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

/**
 * Created by ndarwich on 11/17/17.
 */

//database helper that keeps user logged in and displays their templates
public class LocalUserHelper extends SQLiteOpenHelper {
    final static private Integer VERSION = 1045;
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
    final static String USER_NET_ID = "user_net_id";
    final static String USER_NAME = "user_name";
    final static String USER_LOGGED_IN = "user_logged_in";
    final static String USER_IS_ADMIN = "user_is_admin";
    final static String USER_ICON = "user_photo";
    //==============Reservation Table====================
    final static String TABLE_RESERVATION = "dormbuddy_reservations";
    final static String RESERVATION_TITLE = "reservation_title";
    final static String RESERVATION_DESCRIPTION = "reservation_description";
    final static String RESERVATION_START_TIME = "reservation_start_time";
    final static String RESERVATION_END_TIME = "reservation_end_time";
    final static String RESERVATION_ICON = "reservation_icon";
    final static String RESERVATION_IS_EVENT = "reservation_is_event";
    //==============Marker Table====================
    final static String TABLE_MARKERS = "dormbuddy_markers";
    final static String MARKER_ID = "marker_id";
    final static String MARKER_LATITUDE = "marker_latitude";
    final static String MARKER_LONGITUDE = "marker_longitude";
    final static String MARKER_NAME = "marker_name";
    final static String MARKER_IS_CAMPUS = "marker_is_campus";
    final static String MARKER_IS_IMPORTANT = "marker_is_important";
    //==============Template Table====================
    final static String TABLE_TEMPLATES = "dormbuddy_templates";
    final static String TEMPLATE_ID = "template_id";
    final static String TEMPLATE_NAME = "template_name";
    final static String TEMPLATE_IS_WASHER = "template_is_washer";

    final static String SELECTED_WASHER_TEMPLATE = "washer_template_selected";
    final static String SELECTED_DRYER_TEMPLATE = "dryer_template_selected";









    final private String[] TABLES = {TABLE_BUILDING,TABLE_BUILDING_MAPS,TABLE_ROOM,TABLE_USER,TABLE_RESERVATION,TABLE_MARKERS, TABLE_TEMPLATES};

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
                    USER_ID + " TEXT  NOT NULL, " +
                    USER_NET_ID + " TEXT PRIMARY KEY NOT NULL, " +
                    USER_NAME + " TEXT NOT NULL, " +
                    USER_LOGGED_IN + " INTEGER, " +
                    BUILDING_ID + " TEXT NOT NULL, " +
                    BUILDING_NAME + " TEXT NOT NULL, " +
                    ROOM_NUMBER + " TEXT NOT NULL, " +
                    USER_IS_ADMIN + " BOOLEAN DEFAULT FALSE, " +
                    USER_ICON + " BLOB, " +
                    SELECTED_WASHER_TEMPLATE + " INTEGER DEFAULT -1, " +
                    SELECTED_DRYER_TEMPLATE + " INTEGER DEFAULT -1)";
    final private String CREATE_BUILDING =
            "CREATE TABLE "+TABLE_BUILDING+" ("+
                    BUILDING_ID +" TEXT PRIMARY KEY , "+
                    BUILDING_NAME + " TEXT NOT NULL);";
    final private String CREATE_BUILDING_MAPS =
            "CREATE TABLE "+TABLE_BUILDING_MAPS+" ("+
                    BUILDING_ID +" TEXT NOT NULL, "+
                    BUILDING_MAP_NAME+" INTEGER NOT NULL, "+
                    BUILDING_MAP + " BLOB NOT NULL, "+
                    "PRIMARY KEY ("+BUILDING_ID+", "+BUILDING_MAP_NAME+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_BUILDING+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private String CREATE_ROOM =
            "CREATE TABLE "+TABLE_ROOM+" ("+
                    BUILDING_ID +" TEXT NOT NULL, "+
                    ROOM_NUMBER+" INTEGER NOT NULL, "+
                    ROOM_TYPE+" TEXT CHECK("+ROOM_TYPE+" IN ('"+ROOM_TYPE_LAUNDRY+"','"+ROOM_TYPE_DORM+"','"+ROOM_TYPE_STUDY+"','"+ROOM_TYPE_EVENT+"')),"+
                    ROOM_MAX_OCCUPANCY+" INTEGER CHECK("+ROOM_MAX_OCCUPANCY+" >= 0),"+
                    "PRIMARY KEY ("+BUILDING_ID+", "+ROOM_NUMBER+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_BUILDING+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private  String CREATE_RESERVATION =
            "CREATE TABLE "+TABLE_RESERVATION+" ("+
                    BUILDING_ID +" TEXT NOT NULL, "+
                    ROOM_NUMBER+" TEXT NOT NULL, "+
                    USER_NET_ID+ " TEXT NOT NULL, "+
                    RESERVATION_TITLE+ " TEXT, "+
                    RESERVATION_DESCRIPTION+" TEXT, "+
                    RESERVATION_ICON+ " BLOB, "+
                    RESERVATION_IS_EVENT+" BOOLEAN, "+
                    RESERVATION_START_TIME+ " INTEGER NOT NULL, "+
                    RESERVATION_END_TIME+ " INTEGER NOT NULL, "+
                    "PRIMARY KEY ("+BUILDING_ID+", "+ROOM_NUMBER+","+RESERVATION_START_TIME+","+RESERVATION_END_TIME+"), "+
                    "FOREIGN KEY ("+BUILDING_ID+") REFERENCES "+TABLE_ROOM+" ("+BUILDING_ID+") ON DELETE SET NULL DEFERRABLE,"+
                    "FOREIGN KEY ("+ROOM_NUMBER+") REFERENCES "+TABLE_ROOM+" ("+ROOM_NUMBER+") ON DELETE SET NULL DEFERRABLE,"+
                    "FOREIGN KEY ("+USER_NET_ID+") REFERENCES "+TABLE_USER+" ("+USER_NET_ID+") ON DELETE SET NULL DEFERRABLE);";
    final private String CREATE_MAP =
            "CREATE TABLE "+TABLE_MARKERS+" ("+
                    MARKER_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    MARKER_LATITUDE +" REAL NOT NULL, "+
                    MARKER_LONGITUDE +" REAL NOT NULL, "+
                    MARKER_NAME +" STRING NOT NULL, " +
                    MARKER_IS_CAMPUS +" BOOLEAN, "+
                    MARKER_IS_IMPORTANT +" BOOLEAN);";
    final static String CREATE_TEMPLATE =
            "CREATE TABLE "+TABLE_TEMPLATES+" ("+
                    TEMPLATE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    TEMPLATE_NAME +" STRING NOT NULL, "+
                    TEMPLATE_IS_WASHER +" BOOLEAN);";


    final private String[] CREATE_TABLES = {CREATE_BUILDING,CREATE_BUILDING_MAPS,CREATE_ROOM,CREATE_USER,CREATE_RESERVATION,CREATE_MAP,CREATE_TEMPLATE};

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
       /*
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES( 'FAKE', 'Fake Building');");    //ID 0
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES('BEAC','Beacon Hall');");    //ID 1
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES('BLUE','Blue Ridge');");     //ID 2
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES('COMM','The Commons');");    //ID 3
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES('COMW','Commonwealth');");   //ID 4
        db.execSQL("INSERT INTO "+TABLE_BUILDING+" ("+BUILDING_ID+", "+BUILDING_NAME+") VALUES('DOMI','Dominion');");       //ID 5
        Cursor c = db.rawQuery("SELECT "+BUILDING_ID+" FROM "+TABLE_BUILDING, null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                System.out.println(c.getString(0));
                c.moveToNext();
            }
        }
        // Insert Rooms
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'FAKE','369a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BEAC','222','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BEAC','223','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BEAC','108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BEAC','123','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BEAC','333','EVENT' );");

        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','222','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','333','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','444','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','555','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','111','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','666','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'BLUE','777','EVENT' );");

        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','444','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','69','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','213','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','777','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','666','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','453','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMM','333','EVENT' );");

        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','444a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','444b','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','213','STUDY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','777','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','666','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','453','EVENT' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'COMW','333','EVENT' );");

        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','444a','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','444b','DORM' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','108','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','213','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','777','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','666','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','453','LAUNDRY' );");
        db.execSQL("INSERT INTO "+TABLE_ROOM+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+ROOM_TYPE+") VALUES( 'DOMI','333','LAUNDRY' );");
        // Insert Reservations

        db.execSQL("INSERT INTO "+TABLE_RESERVATION+" ("+BUILDING_ID+", "+ROOM_NUMBER+", "+USER_NET_ID+", "+RESERVATION_TITLE+", "+RESERVATION_DESCRIPTION+
                ", "+RESERVATION_ICON+", "+RESERVATION_IS_EVENT+", "+RESERVATION_START_TIME+", "+RESERVATION_END_TIME+") VALUES( 'COMM','333','jdoe','Test123123','descriptiosnsdfsdf',null,0,"+
                (Calendar.getInstance().getTime().getTime()+100000000)+","+(Calendar.getInstance().getTime().getTime()+500000000)+" );");
       */
        // Insert Markers
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.82962607382459,-77.30722703039646,'JC', 1, 1 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.826365368729114,-77.30419915169477,'Aquatic Center', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.82763921370384,-77.30505410581827,'Engineering Building', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.826059245522934,-77.3089325800538,'Patriot Center', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.82808506711969,-77.31176063418388,'K Lot', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.832092659869765,-77.30726089328527,'Library', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.82607988014902,-77.31490317732096,'University Mall', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.82881091155222,-77.30236385017633,'Ikes', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.83020799106939,-77.31223538517952,'RAC', 1, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.83029365891558,-77.30847861617804,'Campus', 0, 1 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.84671229617381,-77.30404023081064,'Fairfax City Library', 0, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.834737537041775,-77.3070489987731,'Cue Bus Stop', 0, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.839501828815436,-77.31202114373446,'My Off Campus Friend House', 0, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.844649140953415,-77.2752648219466,'Shopping Center', 0, 0 );");
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 38.85086584334248,-77.34768010675907,'Groceries', 0, 0 );"); //wegmans should sponsor this app
        db.execSQL("INSERT INTO "+TABLE_MARKERS+" ("+MARKER_LATITUDE+", "+MARKER_LONGITUDE+", "+MARKER_NAME+", "+MARKER_IS_CAMPUS
                +", "+MARKER_IS_IMPORTANT+")"+"VALUES( 31.9686,-99.9018,'The Great State of Texas', 0, 0 );"); //so should texas
        //
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
