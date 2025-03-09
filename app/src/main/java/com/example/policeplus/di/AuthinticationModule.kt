package com.example.policeplus.di


import TokenManager
import android.content.Context
import com.example.policeplus.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AuthinticationModule {


    @Provides
    fun provideCarRepository(): UserRepository {
        return UserRepository()
    }
    


}
