package com.indopay.qrissapp.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.indopay.qrissapp.core.data.entity.DataLastTrxItemEntity
import com.indopay.qrissapp.core.data.entity.DataTrxItemByDateEntity
import com.indopay.qrissapp.core.data.entity.ProfileMerchantEntity
import com.indopay.qrissapp.core.data.entity.TransactionDetailEntity
import com.indopay.qrissapp.core.data.paging.data_key.RemoteKeys
import com.indopay.qrissapp.core.data.paging.data_key.RemoteKeysDao

@Database(
    entities = [
        ProfileMerchantEntity::class,
        TransactionDetailEntity::class,
        DataLastTrxItemEntity::class,
        DataTrxItemByDateEntity::class,
        RemoteKeys::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class QrisDb : RoomDatabase() {
    abstract fun qrisDao(): QrisDao
    abstract val remoteKeysDao: RemoteKeysDao
}