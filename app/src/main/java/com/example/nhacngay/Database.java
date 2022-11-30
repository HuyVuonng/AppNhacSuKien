package com.example.nhacngay;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    //Truy van khong tra kq
    public void QuerryData(String sql){
        SQLiteDatabase database= getWritableDatabase();
        database.execSQL(sql);
    }

    //Truy van tra kq

    public Cursor GetData(String sql){
        SQLiteDatabase database= getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
