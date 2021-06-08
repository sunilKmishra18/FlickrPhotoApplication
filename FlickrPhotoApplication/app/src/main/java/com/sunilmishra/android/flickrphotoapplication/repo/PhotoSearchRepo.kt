package com.sunilmishra.android.flickrphotoapplication.repo

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.sunilmishra.android.flickrphotoapplication.OpenForTesting
import com.sunilmishra.android.flickrphotoapplication.data.Data
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import com.sunilmishra.android.flickrphotoapplication.repo.remote.PhotoSearchDataSourceFactory
import com.sunilmishra.android.flickrphotoapplication.repo.remote.PhotoSearchRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForTesting
class PhotoSearchRepo @Inject constructor(private val photoSearchRemoteDataSource: PhotoSearchRemoteDataSource) {
    fun searchPhoto(query: String): Data<Photo> {
        Log.e("PhotoSearchRepo", " $query ")
        val dataSourceFactory = PhotoSearchDataSourceFactory(
            photoSearchRemoteDataSource,
            query
        )

        val networkState = Transformations.switchMap(dataSourceFactory.liveData) {
            it.networkState
        }

        return Data(
            LivePagedListBuilder(
                dataSourceFactory,
                PhotoSearchDataSourceFactory.pagedListConfig()
            )
                .build(), networkState
        )

    }
}