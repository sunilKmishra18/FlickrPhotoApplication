package com.sunilmishra.android.flickrphotoapplication.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sunilmishra.android.flickrphotoapplication.di.ViewModelFactory
import com.sunilmishra.android.flickrphotoapplication.di.ViewModelKey
import com.sunilmishra.android.flickrphotoapplication.ui.search.PhotoSearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    @Binds
    @IntoMap
    @ViewModelKey(PhotoSearchViewModel::class)
    abstract fun bindSearchVM(photoSearchViewModel: PhotoSearchViewModel) : ViewModel
}