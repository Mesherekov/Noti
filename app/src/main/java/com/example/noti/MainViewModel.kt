package com.example.noti

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel() {
    private val _stateFlowNoti = MutableStateFlow<List<NotiInfo>>(emptyList())
    val itemsState: StateFlow<List<NotiInfo>> = _stateFlowNoti.asStateFlow()
    override fun onCleared() {
        super.onCleared()
    }

    fun addNoti(context: Context, notiInfo: NotiInfo){
        val notiDatabase = NotiDatabase(context, null)
        val database = notiDatabase.writableDatabase
        val contentValues = ContentValues().apply {
            put(NotiDatabase.HOUR, notiInfo.hour)
            put(NotiDatabase.MINUTE, notiInfo.minute)
            put(NotiDatabase.MESSAGE, notiInfo.message)
            put(NotiDatabase.ISACTIVE, if(notiInfo.isActive) 1 else 0)
            put(NotiDatabase.PERIOD, notiInfo.period)
        }
        database.insert(NotiDatabase.TABLE_NAME, null, contentValues)
        database.close()
        getAllData(context)
    }


    fun updateNoti(context: Context,
                   notiInfo: NotiInfo){
        try {
            val notiDatabase = NotiDatabase(context, null)
            val database = notiDatabase.writableDatabase
            val contentValues = ContentValues().apply {
                put(NotiDatabase.HOUR, notiInfo.hour)
                put(NotiDatabase.MINUTE, notiInfo.minute)
                put(NotiDatabase.MESSAGE, notiInfo.message)
                put(NotiDatabase.ISACTIVE, if (notiInfo.isActive) 1 else 0)
            }
            database.update(
                NotiDatabase.TABLE_NAME,
                contentValues,
                NotiDatabase.ID + "= ?",
                arrayOf(notiInfo.id.toString())
            )
            database.close()
            getAllData(context)
        } catch (ex: Exception){
            Log.e("Error", ex.toString())
        }
    }

    @SuppressLint("Range")
    fun getAllData(context: Context): List<NotiInfo> {
        val dataList = mutableListOf<NotiInfo>()
        val notiDatabase = NotiDatabase(context, null)
        val db = notiDatabase.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${NotiDatabase.TABLE_NAME}", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(NotiDatabase.ID))
                val hour = cursor.getInt(cursor.getColumnIndex(NotiDatabase.HOUR))
                val minute = cursor.getInt(cursor.getColumnIndex(NotiDatabase.MINUTE))
                val isActive = cursor.getInt(cursor.getColumnIndex(NotiDatabase.ISACTIVE))
                val message = cursor.getString(cursor.getColumnIndex(NotiDatabase.MESSAGE))
                val period = cursor.getInt(cursor.getColumnIndex(NotiDatabase.PERIOD))

                dataList.add(
                    NotiInfo(hour,
                        minute,
                        isActive==1,
                        message,
                        period,
                        id
                    )
                )
            } while (cursor.moveToNext())
        }
        _stateFlowNoti.value = dataList
        cursor.close()
        db.close()

        return dataList
    }
    fun deleteNoti(id: Int, context: Context){
        val notiDatabase = NotiDatabase(context, null)
        val db = notiDatabase.readableDatabase
        db.delete(NotiDatabase.TABLE_NAME,
            NotiDatabase.ID + "= ?", arrayOf(id.toString()))
        db.close()
    }

    companion object{
        @SuppressLint("Range")
        fun getAllData(context: Context): List<NotiInfo> {
            val dataList = mutableListOf<NotiInfo>()
            val notiDatabase = NotiDatabase(context, null)
            val db = notiDatabase.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM ${NotiDatabase.TABLE_NAME}", null)
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex(NotiDatabase.ID))
                    val hour = cursor.getInt(cursor.getColumnIndex(NotiDatabase.HOUR))
                    val minute = cursor.getInt(cursor.getColumnIndex(NotiDatabase.MINUTE))
                    val isActive = cursor.getInt(cursor.getColumnIndex(NotiDatabase.ISACTIVE))
                    val message = cursor.getString(cursor.getColumnIndex(NotiDatabase.MESSAGE))
                    val period = cursor.getInt(cursor.getColumnIndex(NotiDatabase.PERIOD))
                    dataList.add(
                        NotiInfo(hour,
                            minute,
                            isActive==1,
                            message,
                            period,
                            id
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return dataList
        }
        fun updateNoti(context: Context,
                       notiInfo: NotiInfo){
            try {
                val notiDatabase = NotiDatabase(context, null)
                val database = notiDatabase.writableDatabase
                val contentValues = ContentValues().apply {
                    put(NotiDatabase.HOUR, notiInfo.hour)
                    put(NotiDatabase.MINUTE, notiInfo.minute)
                    put(NotiDatabase.MESSAGE, notiInfo.message)
                    put(NotiDatabase.ISACTIVE, if (notiInfo.isActive) 1 else 0)
                }
                database.update(
                    NotiDatabase.TABLE_NAME,
                    contentValues,
                    NotiDatabase.ID + "= ?",
                    arrayOf(notiInfo.id.toString())
                )
                database.close()
            } catch (ex: Exception){
                Log.e("Error", ex.toString())
            }
        }
    }
}