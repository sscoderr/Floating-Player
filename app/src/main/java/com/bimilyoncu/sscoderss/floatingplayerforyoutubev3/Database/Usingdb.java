package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.Item.dbFirstLoading;

/**
 * Created by furkan0 on 22.1.2018.
 */

public class Usingdb {
    private static final String LOG_TAG = "UsingDb";
    SQLiteDatabase sqdb;
    myCoderDatabase db;

    public Usingdb(Context context) {
        db = new myCoderDatabase(context);

        openConenct();
    }

    public void openConenct() {
        sqdb = db.getWritableDatabase();
    }

    public void closeConnect() {
        sqdb.close();
    }

    public void addFirstLoading(dbFirstLoading firstLoading) {
        if (firstLoading != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbFirstLoading.cFirstLoading, firstLoading.getFirstLoading());

            if (FirstLoading() != null) {
                sqdb.delete(db.TableFirstLoading, null, null);
                Log.i(LOG_TAG, "FirstLoading Delete..");
            }

            sqdb.insert(db.TableFirstLoading, null, contentValues);
            Log.i(LOG_TAG, "TableFirstLoading Add..");
        } else {
            Log.e(LOG_TAG, "addFirstLoading() -- Null FirstLoading..");
        }
    }

    public dbFirstLoading FirstLoading() {
        Cursor cursor = sqdb.query(db.TableFirstLoading, new String[]{dbFirstLoading.cFirstLoading}, null, null, null, null, null);

        dbFirstLoading firstLoading = null;
        if (cursor.getCount() > 0) {
            firstLoading = new dbFirstLoading();
            cursor.moveToFirst();

            firstLoading.setFirstLoading(cursor.getInt(0));
        }

        return firstLoading;
    }
}
