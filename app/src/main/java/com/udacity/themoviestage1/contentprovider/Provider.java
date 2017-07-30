package com.udacity.themoviestage1.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by farhan on 7/27/17.
 */

public class Provider extends ContentProvider {
    public static final String PROVIDER_NAME = "com.udacity.themoviestage1.contentprovider.Provider";
    public static final String URL = "content://" + PROVIDER_NAME + "/movie";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String TITLE = "key";
    public static final String MOVIE = "movie";
    public static final String IMAGE = "image";

    private static HashMap<String, String> MOVIE_MAP;

    static final int MOVIES = 1;
    static final int MOVIES_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "students", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "students/#", MOVIES_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "The Movie";
    static final String MOVIE_TABLE_NAME = "movie";
    static final int DATABASE_VERSION = 3;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + MOVIE_TABLE_NAME +
                    " ("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " "+TITLE+" TEXT NOT NULL, " +
                    " "+MOVIE+" TEXT NOT NULL, " +
                    " "+IMAGE+" TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE_NAME);
            //onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = db.insert(MOVIE_TABLE_NAME, "", values);

        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MOVIE_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case MOVIES:
                qb.setProjectionMap(MOVIE_MAP);
                break;

            case MOVIES_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            sortOrder = MOVIE;
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db.delete(MOVIE_TABLE_NAME, selection, selectionArgs);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MOVIES:
                count = db.update(MOVIE_TABLE_NAME, values, selection, selectionArgs);
                break;

            case MOVIES_ID:
                count = db.update(MOVIE_TABLE_NAME, values, _ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" +selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case MOVIES:
                return "vnd.android.cursor.dir/vnd.example.students";
            case MOVIES_ID:
                return "vnd.android.cursor.item/vnd.example.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}