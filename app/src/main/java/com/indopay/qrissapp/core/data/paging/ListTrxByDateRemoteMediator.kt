package com.indopay.qrissapp.core.data.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.indopay.qrissapp.core.data.db.QrisDb
import com.indopay.qrissapp.core.data.entity.DataTrxItemByDateEntity
import com.indopay.qrissapp.core.data.paging.data_key.RemoteKeys
import com.indopay.qrissapp.core.network.api.ApiService

@OptIn(ExperimentalPagingApi::class)
class ListTrxByDateRemoteMediator(
    private val authToken: String,
    private val dataMapping: Map<String, String>,
    private val apiService: ApiService,
    private val db: QrisDb
) : RemoteMediator<Int, DataTrxItemByDateEntity>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, DataTrxItemByDateEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeysClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                remoteKeys?.prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val username = dataMapping["username"]
            val mid = dataMapping["MID"]
            val firstDate = dataMapping["firstDate"]
            val lastDate = dataMapping["lastDate"]

            val response = apiService.getTransactionByDate(
                authToken = authToken,
                page = page,
                pageSize = state.config.pageSize,
                username = username!!,
                mId = mid!!,
                firstDate = firstDate!!,
                lastDate = lastDate!!,
            )

            val endOfPaginationReached = response.data?.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao.deleteRemoteKeys()
                    db.qrisDao().deleteAllTrxByDateEntity()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached == true) null else page + 1

                val dataEntity = response.data?.map {
                    it!!.toDataTrxItemByDateEntity(
                        statusResponse = response.status,
                        messageResponse = response.message
                    )
                }

                val newKey = dataEntity?.map {
                    RemoteKeys(
                        id = it.id.toString(),
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                newKey?.let { keys -> db.remoteKeysDao.insertAll(keys) }
                dataEntity?.let {
                    db.qrisDao().insertAllTrxByDateToEntity(it)
                }
            }

            return MediatorResult.Success(endOfPaginationReached!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ListTrxByDateRemoteMediator", "load: ${e.message}")
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, DataTrxItemByDateEntity>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            data.id?.let { id ->
                db.remoteKeysDao.getRemoteKeysId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, DataTrxItemByDateEntity>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            data.id?.let { id ->
                db.remoteKeysDao.getRemoteKeysId(id)
            }
        }
    }

    private suspend fun getRemoteKeysClosestToCurrentPosition(state: PagingState<Int, DataTrxItemByDateEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                db.remoteKeysDao.getRemoteKeysId(id)
            }
        }
    }
}