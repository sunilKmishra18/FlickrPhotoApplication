package com.sunilmishra.android.flickrphotoapplication.repo.remote

import com.sunilmishra.android.flickrphotoapplication.data.BaseDataSource
import com.sunilmishra.android.flickrphotoapplication.data.FlickrApi
import com.sunilmishra.android.flickrphotoapplication.data.Result
import com.sunilmishra.android.flickrphotoapplication.data.SearchResult
import javax.inject.Inject

class PhotoSearchRemoteDataSource @Inject constructor(val service: FlickrApi) : BaseDataSource() {

    val map = HashMap<String,String>()

    init {
        map["method"] = "flickr.photos.search"
        map["api_key"] = "f3ca28946e197f328d1ef2b974535324"
        map["format"] = "json"

    }

    fun search(perPage:Int,query: String,page:Int) : Result<SearchResult> {

        map["text"] = query
        return getResult { service.searchPhotos(perPage,page,map) }
    }

}