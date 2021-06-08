package com.sunilmishra.android.flickrphotoapplication.repo.remote

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import javax.inject.Inject

class PhotoSearchDataSourceFactory @Inject constructor(private val photoSearchRemoteDataSource: PhotoSearchRemoteDataSource,
                                                       private val query: String): DataSource.Factory<Int, Photo>() {
    val liveData = MutableLiveData<PhotoSearchDataSource>()


    override fun create(): DataSource<Int, Photo> {

        val source = PhotoSearchDataSource(photoSearchRemoteDataSource,query)

        liveData.postValue(source)
        return source
    }

    companion object {
        private const val PAGE_SIZE = 30

        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(2 * PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}