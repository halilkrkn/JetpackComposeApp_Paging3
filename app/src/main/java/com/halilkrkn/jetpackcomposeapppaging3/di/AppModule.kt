package com.halilkrkn.jetpackcomposeapppaging3.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.halilkrkn.jetpackcomposeapppaging3.data.local.BeerDatabase
import com.halilkrkn.jetpackcomposeapppaging3.data.local.BeerEntity
import com.halilkrkn.jetpackcomposeapppaging3.data.remote.BeerApi
import com.halilkrkn.jetpackcomposeapppaging3.data.remote.BeerRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBeerDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            BeerDatabase::class.java,
            "beer_database"
        ).build()

    @Provides
    @Singleton
    fun provideBeerApi(): BeerApi {
        return Retrofit.Builder()
            .baseUrl(BeerApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BeerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBeerPager(beerDb: BeerDatabase, beerApi: BeerApi): Pager<Int,BeerEntity> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            remoteMediator = BeerRemoteMediator(
                beerDb,
                beerApi),
            pagingSourceFactory = {
                beerDb.beerDao.pagingSource()
            }
        )
    }
}