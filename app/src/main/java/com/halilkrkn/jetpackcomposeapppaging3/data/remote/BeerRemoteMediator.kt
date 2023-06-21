package com.halilkrkn.jetpackcomposeapppaging3.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.halilkrkn.jetpackcomposeapppaging3.data.local.BeerDatabase
import com.halilkrkn.jetpackcomposeapppaging3.data.local.BeerEntity
import com.halilkrkn.jetpackcomposeapppaging3.data.mappers.toBeerEntity
import kotlinx.coroutines.delay
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class BeerRemoteMediator(
    private val beerDb: BeerDatabase,
    private val beerApi: BeerApi,
) : RemoteMediator<Int, BeerEntity>() {


    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BeerEntity>,
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            delay(2000L)
            val beers = beerApi.getBeers(
                page = loadKey,
                perPage = state.config.pageSize
            )
            beerDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    beerDb.beerDao.deleteAll()
                }
                val beerEntities = beers.map { it.toBeerEntity() }
                beerDb.beerDao.upsertAll(beerEntities)
            }
            MediatorResult.Success(
                endOfPaginationReached = beers.isEmpty()
            )
        } catch (e: Exception) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}