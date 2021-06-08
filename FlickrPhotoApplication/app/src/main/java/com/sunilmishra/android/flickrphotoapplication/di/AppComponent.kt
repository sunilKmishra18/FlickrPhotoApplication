package com.sunilmishra.android.flickrphotoapplication.di

import android.app.Application
import com.sunilmishra.android.flickrphotoapplication.FlickrPhotoApp
import com.sunilmishra.android.flickrphotoapplication.di.modules.ActivityModule
import com.sunilmishra.android.flickrphotoapplication.di.modules.AppModule

import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppModule::class,
        ActivityModule::class]
)
interface AppComponent {


    @Component.Builder
    interface Builder {


        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun appModule(appModule: AppModule): Builder

        fun build(): AppComponent


    }

    fun inject(flickrPhotoApp: FlickrPhotoApp)


}