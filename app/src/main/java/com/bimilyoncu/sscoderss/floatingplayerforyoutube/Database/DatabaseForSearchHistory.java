package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database;

/**
 * Created by Sabahattin on 2/18/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseForSearchHistory extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "searchHistory";

    public DatabaseForSearchHistory(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table textHistory(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "searchText TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void textDelete(String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("textHistory", "searchText = ?",new String[]{String.valueOf(Name)});
        db.close();
    }
    public void textAdd(String text,Context ct) {
        DatabaseForSearchHistory db1 = new DatabaseForSearchHistory(ct);
        SQLiteDatabase readableDatabase = db1.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM textHistory where searchText=?",new String[]{text.toString()});

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("searchText", text);
        if(cur.getCount()!=0)
            db.delete("textHistory", "searchText=?", new String[]{text.toString()});
        db.insert("textHistory", null, values);
        db.close();
    }
}