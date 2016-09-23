package com.matevz.zapisnik;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class SQLiteAdapter extends SQLiteOpenHelper {

    public static final String TABLE_LOGS = "callLogs";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOG_ID = "logId";
    public static final String COLUMN_CALL_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_COMMENT = "comment";

    private static final String DATABASE_NAME = "LOGS";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_LOGS + "( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LOG_ID + " text, "
            + COLUMN_CALL_TYPE + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_NUMBER + " text, "
            + COLUMN_DATE + " text, "
            + COLUMN_COMMENT + " text, "
            + "unique (" + COLUMN_LOG_ID + ")"
            + ");";

    public SQLiteAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteAdapter.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

}