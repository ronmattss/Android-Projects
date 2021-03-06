package com.shadedgames.ronmattss.sampletask.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class TaskProvider extends ContentProvider {

    //First Table
    //Database Columns
    public static final String COLUMN_TASKID = "_id";
    public static final String COLUMN_DATE_TIME = "task_date_time";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TITLE = "title";

    //Database Related Constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "data";
    public static final String DATABASE_TABLE = "tasks";


    // Second Database
    public static final String DATABASE_TABLE2 = "TODOLIST";

    // Columns
    public static final String TODO_ID = "ID";
    public static final String TODO_TITLE = "Title";


    // The Database itself
    SQLiteDatabase db;

    // Content Provider and Authority
    public static final String AUTHORITY =
            "com.shadedgames.ronmattss.sampletask.provider.TaskProvider";
    public static final Uri CONTENT_URI
            = Uri.parse("content://" + AUTHORITY + "/task");

    // MIME types used for isting tasks or ooking up a single task

    private static final String TASKS_MIME_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.shadedgames.ronmattss.sampletask.task";
    private static final String TASK_MIME_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.shadedgames.ronmattss.sampletask.task";
    //URIMatcher Stuff
    private static final int LIST_TASK = 0;
    private static final int ITEM_TASK = 1;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();

    /*
     * Builds up a UriMatcher for search suggestion and shortcut refresh queries
     * */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, "task", LIST_TASK);
        matcher.addURI(AUTHORITY, "task/#", ITEM_TASK);
        return matcher;
    }

    /*
     * This method is required in order to query the supported types
     * */
    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                return TASKS_MIME_TYPE;
            case ITEM_TASK:
                return TASK_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }


    @Override
    public boolean onCreate() {
        db = new DatabaseHelper(getContext()).getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] ignored1, String selection, String[] selectionArgs, String sortOrder) {
        String[] projection = new String[]{COLUMN_TASKID, COLUMN_TITLE, COLUMN_NOTES, COLUMN_DATE_TIME};
        Cursor c;
        switch (URI_MATCHER.match(uri)) {
            case LIST_TASK:
                c = db.query(
                        DATABASE_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder);
                break;
            case ITEM_TASK:
                c = db.query(DATABASE_TABLE, projection,
                        COLUMN_TASKID + "=?",
                        new String[]{Long.toString(ContentUris.parseId(uri))},
                        null,
                        null,
                        null,
                        null);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    public static String getImageUrlForTask(long taskId) {

        return "http://lorempixel.com/600/400/cats/?fakeId=" + taskId;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // you can't choose your own task id
        if (values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();
        long id = db.insertOrThrow(DATABASE_TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String ignored1, String[] ignored2) {
        int count = db.delete(
                DATABASE_TABLE,
                COLUMN_TASKID + "=?",
                new String[]{Long.toString(ContentUris.parseId(uri))});

        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);


        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String ignored1, String[] ignored2) {
        // you can't edit task id
        if (values.containsKey(COLUMN_TASKID))
            throw new UnsupportedOperationException();
        long id = ContentUris.parseId(uri);
        int count = db.update(
                DATABASE_TABLE,
                values,
                COLUMN_TASKID + "=?",
                new String[]{Long.toString(id)});
        if (count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}

    /**
     * A helper class which knows how to create and update our database.
     */
   /* public static class DatabaseHelper extends SQLiteOpenHelper
    {
        static final String DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (" +
                        COLUMN_TASKID + " integer primary key autoincrement, " +
                        COLUMN_TITLE + " text not null, " +
                        COLUMN_NOTES + " text not null, " +
                        COLUMN_DATE_TIME + " integer not null);";
     static final String DATABASE_CREATE_TABLE2 =
                "create table " + DATABASE_TABLE2 + " (" +
                        TODO_ID + " integer primary key autoincrement, " +
                        TODO_TITLE + " text not null);";

         public DatabaseHelper(Context context)
        {
                super(context,DATABASE_NAME,null,DATABASE_VERSION);

        }




        @Override
        public void onCreate(SQLiteDatabase db)

        {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE_TABLE2);

          ///  Toast.makeText(getContext(), "Table Created", Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion)
        {
            throw new UnsupportedOperationException();
        }


        public  final void addDataToTodo(String input)
        {

            SQLiteDatabase database = getWritableDatabase();
            ContentValues values = new ContentValues();
            long id = database.insertWithOnConflict(DATABASE_TABLE2,null,values,SQLiteDatabase.CONFLICT_IGNORE);
            values.put(TODO_ID,id);
            values.put(TITLE,input);
            database.insert(DATABASE_TABLE2,null,values);
            database.close();
        }

        public Cursor getData() {
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT * FROM " + DATABASE_TABLE2;
            Cursor data = db.rawQuery(query, null);
            return data;
        }

        public Cursor getItemID(String name){
            SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT " + TODO_ID + " FROM " + DATABASE_TABLE2 +
                    " WHERE " + TITLE + " = '" + name + "'";
            Cursor data = db.rawQuery(query, null);
            return data;
        }

        *
         * Updates the name field
         * @param newName
         * @param id
         * @param oldName

        public void updateName(String newName, int id, String oldName){
            SQLiteDatabase db = getWritableDatabase();
            String query = "UPDATE " + DATABASE_TABLE2 + " SET " + TITLE +
                    " = '" + newName + "' WHERE " + TODO_ID + " = '" + id + "'" +
                    " AND " + TITLE + " = '" + oldName + "'";
            db.execSQL(query);
        }

        *
         * Delete from database
         * @param id
         * @param name

        public void deleteName(int id, String name){
            SQLiteDatabase db = getWritableDatabase();
            String query = "DELETE FROM " + DATABASE_TABLE2 + " WHERE "
                    + TODO_ID + " = '" + id + "'" +
                    " AND " + TITLE + " = '" + name + "'";
            db.execSQL(query);
        }



    }*/




