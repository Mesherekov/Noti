package com.example.noti

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotiDatabase(context: Context, factory: SQLiteDatabase.CursorFactory?):
    SQLiteOpenHelper(context,
    "noti",
    factory,
    1) {
    val TABLE_NAME = "notidb"
    val id = "id"
    val hour = "hour"
    val minute = "minute"
    val isActive = "isActive"
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TABLE_NAME($id integer primary key,$hour integer,$minute integer,$isActive integer)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}