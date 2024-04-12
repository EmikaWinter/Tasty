package com.tms.an16.tasty.di

import com.tms.an16.tasty.network.Api
import com.tms.an16.tasty.util.Constants.Companion.API_KEY
import com.tms.an16.tasty.util.Constants.Companion.BASE_URL
import com.tms.an16.tasty.util.Constants.Companion.QUERY_API_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideApi(): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .addInterceptor(Interceptor { chain ->
                        val request = chain.request()
                        val url = request.url.newBuilder().addQueryParameter(QUERY_API_KEY, API_KEY)
                            .build()
                        val newRequest = request.newBuilder().url(url).build()
                        chain.proceed(newRequest)
                    })
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}