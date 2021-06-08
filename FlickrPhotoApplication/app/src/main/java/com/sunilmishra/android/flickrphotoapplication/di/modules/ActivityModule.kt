package com.sunilmishra.android.flickrphotoapplication.di.modules

import com.sunilmishra.android.flickrphotoapplication.ui.search.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}