package com.sunilmishra.android.flickrphotoapplication.di.modules

import com.sunilmishra.android.flickrphotoapplication.data.FlickrApi
import com.sunilmishra.android.flickrphotoapplication.di.NetworkModule
import com.sunilmishra.android.flickrphotoapplication.repo.remote.PhotoSearchRemoteDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideGithubService(okhttpClient: OkHttpClient,
                             converterFactory: GsonConverterFactory
    ) = provideService(okhttpClient, converterFactory, FlickrApi::class.java)

    private fun createRetrofit(
        okhttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FlickrApi.BASE_URL)
            .client(okhttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideSearchRemoteDataSource(flickrApi: FlickrApi)
            = PhotoSearchRemoteDataSource(flickrApi)

    @Provides
    fun provideCoroutineScopeIO() = CoroutineScope(Dispatchers.Main)

    private fun <T> provideService(okhttpClient: OkHttpClient,
                                   converterFactory: GsonConverterFactory, clazz: Class<T>): T {
        return createRetrofit(okhttpClient, converterFactory).create(clazz)
    }



}