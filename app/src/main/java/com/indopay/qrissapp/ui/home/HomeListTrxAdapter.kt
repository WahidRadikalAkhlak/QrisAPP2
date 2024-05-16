package com.indopay.qrissapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ItemListTrxBinding
import com.indopay.qrissapp.domain.model.DataLastTransactionItem


class HomeListTrxAdapter(private val onClickItem: (DataLastTransactionItem) -> Unit) :
    ListAdapter<DataLastTransactionItem, HomeListTrxAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataLastTransactionItem>() {
            override fun areItemsTheSame(oldItem: DataLastTransactionItem, newItem: DataLastTransactionItem): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: DataLastTransactionItem,
                newItem: DataLastTransactionItem
            ): Boolean =
                oldItem == newItem

        }
    }

    class ViewHolder(private val binding: ItemListTrxBinding, val onClickItem: (DataLastTransactionItem) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataLastTransactionItem) {
            with(binding) {
                binding.trxListStatus.text = item.status
                binding.dateTrxList.text = item.date
                binding.trxListId.text =
                    StringBuilder(itemView.context.getString(R.string.transaction_id)).append(" ").append(item.idTrx)
                binding.nominalTrxList.text = item.amount
                itemView.setOnClickListener {
                    onClickItem(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListTrxBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), onClickItem
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}