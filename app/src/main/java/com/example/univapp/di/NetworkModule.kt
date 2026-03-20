package com.example.univapp.di

import com.example.univapp.data.network.MapsApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.EntryPoint
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MapsEntryPoint {
    fun mapsApiService(): MapsApiService
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Puente estático para facilitar la migración de funciones globales
    private var _mapsApiService: MapsApiService? = null
    val mapsApiService: MapsApiService
        get() = _mapsApiService ?: throw IllegalStateException("MapsApiService not initialized")

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().setLenient().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        val certificatePinner = CertificatePinner.Builder()
            .add("maps.googleapis.com", "sha256/7hi6thOf97InqM9SWfJAJLS0voS8ZLo8yInS/1cy6sk=")
            .add("router.project-osrm.org", "sha256/Woi6yJ0FA9B7KBfWVkAnkthSefLW6suHB88WhotSvlE=")
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .certificatePinner(certificatePinner)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideMapsApiService(retrofit: Retrofit): MapsApiService {
        val service = retrofit.create(MapsApiService::class.java)
        _mapsApiService = service
        return service
    }
}
