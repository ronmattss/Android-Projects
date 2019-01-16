package com.shadedgames.ronmattss.sampletask.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.shadedgames.ronmattss.sampletask.provider.TaskProvider.DATABASE_NAME;
import static com.shadedgames.ronmattss.sampletask.provider.TaskProvider.DATABASE_TABLE2;
import static com.shadedgames.ronmattss.sampletask.provider.TaskProvider.DATABASE_VERSION;
import static com.shadedgames.ronmattss.sampletask.provider.TaskProvider.TODO_ID;
import static com.shadedgames.ronmattss.sampletask.provider.TaskProvider.TODO_TITLE;

public class DatabaseHelper extends SQLiteOpenHelper {

    static final String DATABASE_CREATE =
            "create table " + TaskProvider.DATABASE_TABLE + " (" +
                    TaskProvider.COLUMN_TASKID + " integer primary key autoincrement, " +
                    TaskProvider.COLUMN_TITLE + " text not null, " +
                    TaskProvider.COLUMN_NOTES + " text not null, " +
                    TaskProvider.COLUMN_DATE_TIME + " integer not null);";
    static final String DATABASE_CREATE_TABLE2 =
            "create table " + TaskProvider.DATABASE_TABLE2 + " (" +
                    TaskProvider.TODO_ID + " integer primary key autoincrement, " +
                    TaskProvider.TODO_TITLE + " text not null);";

    public DatabaseHelper( Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE_TABLE2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException();

    }
    public boolean addData(String item)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TODO_TITLE,item);
        long result = db.insert(DATABASE_TABLE2,null,values);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + DATABASE_TABLE2;
        Cursor data = db.rawQuery(query,null);
        return  data;
    }
    /**
     * Returns only the ID that matches the name passed in
     * @param name
     * @return
     */
    public Cursor getItemID(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + TODO_ID + " FROM " + DATABASE_TABLE2 +
                " WHERE " + TODO_TITLE + " = '" + name + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    /**
     * Updates the name field
     * @param newName
     * @param id
     * @param oldName
     */
    public void updateName(String newName, int id, String oldName){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + DATABASE_TABLE2 + " SET " + TODO_TITLE +
                " = '" + newName + "' WHERE " + TODO_ID + " = '" + id + "'" +
                " AND " + TODO_TITLE + " = '" + oldName + "'";
        db.execSQL(query);
    }
    public void deleteName(int id, String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + DATABASE_TABLE2 + " WHERE "
                + TODO_ID + " = '" + id + "'" +
                " AND " + TODO_TITLE + " = '" + name + "'";
        db.execSQL(query);
    }

}
