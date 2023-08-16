/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Dao
interface VideoDao {

    //trying to return a List will try to retrieve database on main thread, changing it to
    // live data means we can do it on a different thread
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    //vararg is how a fun can take an unknown number of arguments in kotlin, it'll pass an array under the hood
    @Insert(onConflict = OnConflictStrategy.REPLACE) //overwrite/replace the last saved value with the new one
    suspend fun insertAll(vararg videos: DatabaseVideo)
}
@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase: RoomDatabase() {
    abstract val videoDao: VideoDao
}
private lateinit var INSTANCE: VideosDatabase

fun getDatabase(context: Context): VideosDatabase {
    synchronized(VideosDatabase::class.java){ //synchronized makes it thread safe
        if (!::INSTANCE.isInitialized) { //for kotlin singleton variables
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                VideosDatabase::class.java,
                "videos").build()
        }
    }
    return INSTANCE
}