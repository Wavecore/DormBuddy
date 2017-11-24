package com.cs477.dormbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ndarwich on 11/17/17.
 */

//database helper that keeps user logged in and displays their templates
public class LocalUserHelper extends SQLiteOpenHelper {
    final static private Integer VERSION = 54;
    final static String TABLE_NAME = "dormbuddy_user";
    final static String _ID = "_id";
    final static String FULL_NAME = "student_name";
    final static String LOGGED_IN = "logged_in";
    final static String BUILDING_ID = "building_id";
    final static String BUILDING_NAME = "building_name";
    final static String ROOM_NUMBER = "room_number";
    final static String ICON = "user_photo";
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
    final private static String CREATE_CMD =
            "CREATE TABLE dormbuddy_user (" +
                    _ID + " INTEGER PRIMARY KEY, " +
                    FULL_NAME + " TEXT NOT NULL, " +
                    LOGGED_IN + " INTEGER DEFAULT 0, " +
                    BUILDING_ID + " INTEGER DEFAULT 0, " +
                    BUILDING_NAME + " TEXT NOT NULL, " +
                    ROOM_NUMBER + " TEXT NOT NULL, " +
                    ICON + " BLOB, " +
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
            ;

    public LocalUserHelper(Context context) {
        super(context, TABLE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dormbuddy_user");
        onCreate(db);
    }

    void deleteDatabase ( ) {
        context.deleteDatabase(TABLE_NAME);
    }
}
