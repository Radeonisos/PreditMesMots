package com.entreprise.davfou.preditmesmots

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

/**
 * Created by tensa on 06/06/2018.
 */

@Dao
interface DataDAO{
    @Query("SELECT * FROM data;")
    fun getAll():List<Data>

    @Insert(onConflict = REPLACE)
    fun insert(data: Data)
}