package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database;

import android.database.sqlite.SQLiteDatabase;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.Item.dbFirstLoading;

/**
 * Created by furkan0 on 22.1.2018.
 */

public class myCoderCreateTable {
    public static void TableFirstLoading(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + myCoderDatabase.TableFirstLoading + "( " +
                dbFirstLoading.cFirstLoading + " Integer)");
    }
}
