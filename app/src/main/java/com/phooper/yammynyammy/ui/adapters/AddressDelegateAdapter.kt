package com.phooper.yammynyammy.ui.adapters

import android.annotation.SuppressLint
import com.livermor.delegateadapter.delegate.KDelegateAdapter
import com.phooper.yammynyammy.R
import com.phooper.yammynyammy.domain.models.Address
import kotlinx.android.synthetic.main.item_address.*
import timber.log.Timber

data class AddressDelegateAdapter(
    private val onItemClickListener: (String) -> (Unit),
    private val onEditBtnClickListener: (String) -> (Unit)
) :
    KDelegateAdapter<Address>() {
    override fun getLayoutId() = R.layout.item_address

    @SuppressLint("SetTextI18n")
    override fun KViewHolder.onBind(item: Address) {
        address.text = "ул. ${item.street}, д. ${item.houseNum} кв. ${item.apartNum}"
        item_layout.setOnClickListener {
            onItemClickListener.invoke(item.uid)
        }
        edit_btn.setOnClickListener {
            onEditBtnClickListener.invoke(item.uid)
        }

    }

    override fun isForViewType(item: Any): Boolean = item is Address
}

