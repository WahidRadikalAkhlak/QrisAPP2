package com.indopay.qrissapp.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ItemListTrxBinding
import com.indopay.qrissapp.domain.model.DataLastTransactionItem

class LastTrxAdapterWithPaging(private val onClickItem: (DataLastTransactionItem) -> Unit) :
    PagingDataAdapter<DataLastTransactionItem, LastTrxAdapterWithPaging.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataLastTransactionItem>() {
            override fun areItemsTheSame(
                oldItem: DataLastTransactionItem,
                newItem: DataLastTransactionItem
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: DataLastTransactionItem,
                newItem: DataLastTransactionItem
            ): Boolean =
                oldItem == newItem

        }
    }

    class ViewHolder(
        private val binding: ItemListTrxBinding,
        val onClickItem: (DataLastTransactionItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataLastTransactionItem?) {
            with(binding) {
                dateTrxList.text = item?.date
                netAmount.text = item?.netAmount
                nominalTrxList.text = item?.amount
                trxListId.text = StringBuilder(itemView.context.getString(R.string.transaction_id))
                    .append(item?.idTrx)
                trxListStatus.text = item?.status

                itemView.setOnClickListener {
                    item?.let { onClickItem(it) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListTrxBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}