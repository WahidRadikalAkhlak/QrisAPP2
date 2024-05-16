package com.indopay.qrissapp.ui.transaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.indopay.qrissapp.R
import com.indopay.qrissapp.databinding.ItemListTrxBinding
import com.indopay.qrissapp.domain.model.DataTrxItemByDate

class TrxListByDateAdapter(private val onClick: (DataTrxItemByDate) -> Unit) :
    PagingDataAdapter<DataTrxItemByDate, TrxListByDateAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataTrxItemByDate>() {
            override fun areItemsTheSame(oldItem: DataTrxItemByDate, newItem: DataTrxItemByDate): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(oldItem: DataTrxItemByDate, newItem: DataTrxItemByDate): Boolean =
                oldItem == newItem

        }
    }

    class ViewHolder(private val binding: ItemListTrxBinding, private val onClick: (DataTrxItemByDate) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataTrxItemByDate?) {
            with(binding) {
                trxListStatus.text = item?.status
                netAmount.text = item?.netAmount
                trxListId.text =
                    StringBuilder(itemView.context.getString(R.string.transaction_id)).append(item?.idTrx)
                nominalTrxList.text = item?.amount
                dateTrxList.text = item?.date

                itemView.setOnClickListener {
                    item?.let { item -> onClick(item) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListTrxBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}