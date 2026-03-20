package com.example.univapp.di

import android.content.Context
import com.example.univapp.data.LocalStore
import com.example.univapp.data.SessionManager
import com.example.univapp.data.repository.GroupsRepository
import com.example.univapp.data.repository.GroupsRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager = SessionManager(context)

    @Provides
    @Singleton
    fun provideLocalStore(@ApplicationContext context: Context): LocalStore = LocalStore(context)

    @Provides
    @Singleton
    fun provideGroupsRepository(db: FirebaseFirestore): GroupsRepository = GroupsRepositoryImpl(db)
}
