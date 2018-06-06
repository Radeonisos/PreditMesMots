package com.entreprise.davfou.preditmesmots

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

/**
 * Created by tensa on 06/06/2018.
 */

@Database(entities = arrayOf(Data::class), version = 1)

abstract class Database: RoomDatabase(){
    abstract fun DataDAO(): DataDAO

    companion object {
        private var INSTANCE:com.entreprise.davfou.preditmesmots.Database?=null
        fun getInstance(context: Context) : com.entreprise.davfou.preditmesmots.Database?{
            if(INSTANCE==null){
                synchronized(com.entreprise.davfou.preditmesmots.Database::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,com.entreprise.davfou.preditmesmots.Database::class.java,
                            "data.db").build()
                }
            }
            return INSTANCE
        }
        fun destroyInstance(){
            INSTANCE = null
        }
    }
}