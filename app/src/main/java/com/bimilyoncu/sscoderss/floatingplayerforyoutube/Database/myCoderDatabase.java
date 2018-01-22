package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by furkan0 on 22.1.2018.
 */

public class myCoderDatabase extends SQLiteOpenHelper {
    private final static int SURUM = 1;
    private static final String DATABASE = "myCoder";
    public static final String TableFirstLoading = "firstLoading";

    public myCoderDatabase(Context context) {
        super(context, DATABASE, null, SURUM);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        myCoderCreateTable.TableFirstLoading(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
