package com.matevz.zapisnik;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LogsDataSource {

    private SQLiteDatabase database;
    private SQLiteAdapter dbHelper;
    private String[] allColumns = {
            SQLiteAdapter.COLUMN_ID,
            SQLiteAdapter.COLUMN_LOG_ID,
            SQLiteAdapter.COLUMN_CALL_TYPE,
            SQLiteAdapter.COLUMN_NAME,
            SQLiteAdapter.COLUMN_NUMBER,
            SQLiteAdapter.COLUMN_DATE,
            SQLiteAdapter.COLUMN_COMMENT};

    public LogsDataSource(Context context) {
        dbHelper = new SQLiteAdapter(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Log createLog(String logId, String callType, String name, String number, String date, String comment) {
        ContentValues values = new ContentValues();
        values.put(SQLiteAdapter.COLUMN_LOG_ID, logId);
        values.put(SQLiteAdapter.COLUMN_CALL_TYPE, callType);
        values.put(SQLiteAdapter.COLUMN_NAME, name);
        values.put(SQLiteAdapter.COLUMN_NUMBER, number);
        values.put(SQLiteAdapter.COLUMN_DATE, date);
        values.put(SQLiteAdapter.COLUMN_COMMENT, comment);

        long insertId = database.insert(SQLiteAdapter.TABLE_LOGS, null, values);

        Cursor cursor = database.query(SQLiteAdapter.TABLE_LOGS, allColumns, SQLiteAdapter.COLUMN_ID + " = " + insertId,
                null, null, null, null);
        cursor.moveToFirst();
        Log log = cursorToLog(cursor);
        cursor.close();
        return log;
    }

    public void updateLog(Log log, String comment) {
        long id = log.getId();

        ContentValues values = new ContentValues();
        values.put(SQLiteAdapter.COLUMN_COMMENT, comment);

        database.update(SQLiteAdapter.TABLE_LOGS, values, SQLiteAdapter.COLUMN_ID + " = " + id, null);
    }

    public void deleteLog(Log log){
        long id = log.getId();
        ContentValues values = new ContentValues();
        values.put(SQLiteAdapter.COLUMN_COMMENT, "");
        database.update(SQLiteAdapter.TABLE_LOGS, values, SQLiteAdapter.COLUMN_ID + " = " + id, null);
    }

    public List<Log> getAllLogs() {
        List<Log> logs = new ArrayList<>();

        Cursor cursor = database.query(SQLiteAdapter.TABLE_LOGS, allColumns,
                null, null, null, null, SQLiteAdapter.COLUMN_DATE + " DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log log = cursorToLog(cursor);
            logs.add(log);
            cursor.moveToNext();
        }

        cursor.close();
        return logs;
    }

    private Log cursorToLog(Cursor cursor) {
        Log log = new Log();
        log.setId(cursor.getLong(0));
        log.setLogId(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_LOG_ID)));
        log.setCallType(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_CALL_TYPE)));
        log.setName(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_NAME)));
        log.setNumber(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_NUMBER)));
        log.setDate(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_DATE)));
        log.setComment(cursor.getString(cursor.getColumnIndex(SQLiteAdapter.COLUMN_COMMENT)));
        return log;
    }
}

