package com.entreprise.davfou.preditmesmots

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by tensa on 06/06/2018.
 */

@Entity(tableName = "data")
data class Data(@PrimaryKey(autoGenerate = true) var id: Int?,
                @ColumnInfo(name = "word1") var word1: String,
                @ColumnInfo(name = "word2") var word2: String,
                @ColumnInfo(name = "count") var count: Int){
    constructor():this(null,"","",0)
}
