package com.sunilmishra.android.flickrphotoapplication.repo.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.sunilmishra.android.flickrphotoapplication.data.NetworkState
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import com.sunilmishra.android.flickrphotoapplication.data.Result

class PhotoSearchDataSource @Inject constructor(

    private val remoteDataSource: PhotoSearchRemoteDataSource,
    private val query: String

) : PageKeyedDataSource<Int, Photo>() {


    val networkState = MutableLiveData<NetworkState>()

    override fun loadInitial (
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Photo>
    ) {
        networkState.postValue(NetworkState.LOADING)
        fetchData(query,1, params.requestedLoadSize) {
            callback.onResult(it, null, 2)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {
        networkState.postValue(NetworkState.LOADING)
        val page = params.key
        fetchData(query,page, params.requestedLoadSize) {

            callback.onResult(it, page + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Photo>) {


    }

    private fun fetchData(query: String,page: Int, pageSize: Int, callback: (List<Photo>) -> Unit) {

        val response = remoteDataSource.search(pageSize,query,page)

        Log.e("SearchRepo"," ${response.status}")


        if (response.status == Result.Status.SUCCESS) {

            val results = response.data?.photos?.photos

            if(results.isNullOrEmpty()) {
                networkState.postValue(NetworkState.noData())
            } else {
                callback(results)
                networkState.postValue(NetworkState.LOADED)
            }

        } else if (response.status == Result.Status.ERROR) {
            networkState.postValue(NetworkState.error(response.message ?: "unknown err"))
            postError(response.message!!)
        }
    }

    private fun getJobErrorHandler() = CoroutineExceptionHandler { _, e ->
        postError(e.message ?: e.toString())
    }

    private fun postError(message: String) {

        networkState.postValue(NetworkState.error(message))
    }
}
