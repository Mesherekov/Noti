package com.example.noti

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotiDatabase(context: Context, factory: SQLiteDatabase.CursorFactory?):
    SQLiteOpenHelper(context,
    NOTI_NAME,
    factory,
    VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table $TABLE_NAME($ID integer primary key, $HOUR integer, $MINUTE integer, $ISACTIVE integer, $MESSAGE text, $PERIOD integer)")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    companion object{
        const val VERSION = 4
        const val NOTI_NAME = "noti"
        const val TABLE_NAME = "notidb"
        const val ID = "_id"
        const val HOUR = "hour"
        const val PERIOD = "period"
        const val MINUTE = "minute"
        const val MESSAGE = "message"
        const val ISACTIVE = "isActive"
    }
}