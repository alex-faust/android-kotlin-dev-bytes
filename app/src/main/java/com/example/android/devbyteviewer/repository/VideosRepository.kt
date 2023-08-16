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

package com.example.android.devbyteviewer.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.devbyteviewer.database.VideosDatabase
import com.example.android.devbyteviewer.database.asDomainModel
import com.example.android.devbyteviewer.domain.Video
import com.example.android.devbyteviewer.network.Network
import com.example.android.devbyteviewer.network.asDatabaseModel

//repo for fetching devbyte videos from the network and storing them on the disk
//dependency injection
//passing database as a constructor paramenter so you dont need to keep a reference to android context in our repo causing leaks
class VideosRepository(private val database: VideosDatabase) {

    //dont need to make a method for this since live data is listening and only runs query when activity or fragment is listening
    //we want to give video entity, not network video or database video because they are implemntation details of the repo and the repo is supposed to hide them
    val videos: LiveData<List<Video>> = database.videoDao.getVideos().map {
        it.asDomainModel()
    }

    //refreshes offline cache. no return so its not accidentally used by the api to fetch videos
    suspend fun refreshVideos() {
        //network call
        val playlist = Network.devbytes.getPlaylist()
        //database call
        database.videoDao.insertAll(*playlist.asDatabaseModel()) //Note the asterisk * is the spread operator. It allows you to pass in an array to a function that expects varargs.
    }
}
