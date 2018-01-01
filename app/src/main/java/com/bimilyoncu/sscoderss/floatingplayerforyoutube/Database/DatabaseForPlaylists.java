package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sabahattin on 2.03.2017.
 */

public class DatabaseForPlaylists extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "playlistsDB";

    public DatabaseForPlaylists(Context ct){
        super(ct, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create Table tbl_favorite(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT)");
        db.execSQL("Create Table tbl_history(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT)");
        db.execSQL("Create Table tbl_recommented(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT)");
        db.execSQL("Create Table tbl_fromyoutube_videos(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "videoId TEXT,playlistId INTEGER)");
        db.execSQL("Create Table tbl_fromyoutube_playlist(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "playlistName TEXT,videoCount INTEGER)");
        String query = "CREATE TRIGGER if not exists deleteEmptyPlaylist "
                + " AFTER UPDATE "
                + " ON tbl_fromyoutube_playlist "
                + " BEGIN "
                + " delete from tbl_fromyoutube_playlist where videoCount=0;"
                + " END;";
        db.execSQL(query);


    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void addVideoFavorites(String text,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_favorite where videoId=?",new String[]{text.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", text);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_favorite", null, values);
        db.close();
    }
    public void addVideoHistory(String text,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_history where videoId=?",new String[]{text.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", text);
        if(cur.getCount()!=0)
            dbTwo.delete("tbl_history","videoId=?",new String[]{text.toString()});
        dbTwo.insert("tbl_history", null, values);
        db.close();
    }
    public void addVideoRecommented(String text,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_recommented where videoId=?",new String[]{text.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", text);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_recommented", null, values);
        db.close();
    }
    public void addUserVideo(String text,Context ct,String playlistId) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_videos where videoId=? and playlistId=?",new String[]{text.toString(),playlistId});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("videoId", text);
        values.put("playlistId",playlistId);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_fromyoutube_videos", null, values);
        db.close();
    }
    public void addUserPlaylist(String playlistName,int videoCount,Context ct) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?",new String[]{playlistName.toString()});

        SQLiteDatabase dbTwo = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("playlistName", playlistName);
        values.put("videoCount",videoCount);
        if(cur.getCount()==0)
            dbTwo.insert("tbl_fromyoutube_playlist", null, values);
        else {
            SQLiteDatabase dbForUpdate = this.getWritableDatabase();
            Cursor cursorOne = dbForUpdate.rawQuery("SELECT id FROM tbl_fromyoutube_playlist where playlistName=?",new String[]{playlistName});
            int playlistIdTemb=0;
            if( cursorOne != null && cursorOne.moveToFirst() ) {
                playlistIdTemb = Integer.parseInt(cursorOne.getString(0));
                cur.close();
            }

            dbTwo.delete("tbl_fromyoutube_playlist","playlistName=?",new String[]{playlistName});
            dbTwo.insert("tbl_fromyoutube_playlist", null, values);



            String query="SELECT id FROM tbl_fromyoutube_playlist order by id desc LIMIT 1";
            Cursor cursor = dbForUpdate.rawQuery(query,null);
            int playlistId=0;
            if( cursor != null && cursor.moveToFirst() ) {
                playlistId = Integer.parseInt(cursor.getString(0));
                cur.close();
            }

            ContentValues value = new ContentValues();
            value.put("playlistId",playlistId);
            dbForUpdate.update("tbl_fromyoutube_videos",value,"playlistId=?",new String[]{String.valueOf(playlistIdTemb)});
            dbForUpdate.close();
        }
        db.close();
    }
    public void videoDeleteFromFavs(String videoId,String tblName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tblName, "videoId = ?",new String[]{String.valueOf(videoId)});
        db.close();
    }
    public void updateVideoCount(String playlistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("update tbl_fromyoutube_playlist set videoCount=(select videoCount from tbl_fromyoutube_playlist where playlistName=?)-1 where playlistName=?",new String[]{playlistName,playlistName});
    }
}
