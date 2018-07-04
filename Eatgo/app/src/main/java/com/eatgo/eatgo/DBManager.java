package com.eatgo.eatgo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBManager extends SQLiteOpenHelper {


    public DBManager(Context context) {

        super(context, "eatgoDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE eatgo (id TEXT PRIMARY KEY, pw TEXT, kor INT, jap INT, chn INT,  wes INT, etc INT, " +
                "kore INT, jape INT, chne INT, wese INT, etce INT);");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}