package com.sunilmishra.android.flickrphotoapplication.ui.search

import androidx.lifecycle.ViewModel
import com.sunilmishra.android.flickrphotoapplication.data.Data
import com.sunilmishra.android.flickrphotoapplication.data.Photo
import com.sunilmishra.android.flickrphotoapplication.repo.PhotoSearchRepo
import javax.inject.Inject

class PhotoSearchViewModel @Inject constructor(private val photoSearchRepo: PhotoSearchRepo) : ViewModel() {
    var data: Data<Photo>? = null

    var oldQuery: String = ""

    fun search(query: String): Data<Photo>? {
        if (data == null || oldQuery != query)
            data = photoSearchRepo.searchPhoto(query)

        oldQuery = query
        return data
    }

}