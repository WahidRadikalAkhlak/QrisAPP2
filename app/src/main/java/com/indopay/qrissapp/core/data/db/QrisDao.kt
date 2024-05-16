package com.indopay.qrissapp.core.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.indopay.qrissapp.core.data.entity.DataLastTrxItemEntity
import com.indopay.qrissapp.core.data.entity.DataTrxItemByDateEntity
import com.indopay.qrissapp.core.data.entity.ProfileMerchantEntity
import com.indopay.qrissapp.core.data.entity.TransactionDetailEntity

@Dao
interface QrisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfileMerchantToEntity(dataProfile: ProfileMerchantEntity)

    @Query("SELECT * FROM profile_merchant_entity")
    suspend fun getProfileMerchantFromEntity() : ProfileMerchantEntity?

    @Query("DELETE FROM profile_merchant_entity")
    suspend fun deleteProfileMerchantEntity()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactionDetailToEntity(dataTrx: TransactionDetailEntity)

    @Query("SELECT * FROM trx_detail_entity WHERE idTrx = :idTrx")
    suspend fun getTrxDetailFromEntity(idTrx: String?) : TransactionDetailEntity?

    @Query("DELETE FROM trx_detail_entity WHERE idTrx = :idTrx")
    suspend fun deleteTrxDetailFromEntity(idTrx: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLastTransactionToEntity(listTrx: List<DataLastTrxItemEntity>)

    @Query("SELECT * FROM last_trx_entity")
    suspend fun getAllLastTransactionFromEntity() : List<DataLastTrxItemEntity>

    @Query("SELECT * FROM last_trx_entity")
    fun getAllLastTransactionFromEntityUsingPaging() : PagingSource<Int, DataLastTrxItemEntity>

    @Query("DELETE FROM last_trx_entity")
    suspend fun deleteAllLastTrxFromEntity()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrxByDateToEntity(listTrx: List<DataTrxItemByDateEntity>)

    @Query("SELECT * FROM trx_item_byDate_entity")
    fun getAllTransactionByDateFromEntity() : PagingSource<Int, DataTrxItemByDateEntity>

    @Query("DELETE FROM trx_item_byDate_entity")
    suspend fun deleteAllTrxByDateEntity()
}