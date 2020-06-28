package com.phooper.yammynyammy.viewmodels

import androidx.lifecycle.*
import com.livermor.delegateadapter.delegate.diff.KDiffUtilItem
import com.phooper.yammynyammy.data.models.ProductIdAndCount
import com.phooper.yammynyammy.data.models.ProductInCart
import com.phooper.yammynyammy.data.models.TotalAndDeliveryPrice
import com.phooper.yammynyammy.data.repositories.ProductsRepository
import com.phooper.yammynyammy.data.repositories.UserRepository
import com.phooper.yammynyammy.utils.Constants.Companion.DELIVERY_PRICE
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartViewModel(
    private val userRepository: UserRepository,
    private val productsRepository: ProductsRepository
) : ViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state: LiveData<ViewState> get() = _state


    val productsInCart: LiveData<MutableList<KDiffUtilItem>> =
        userRepository.getAllCartProducts().switchMap { prodAndCountList ->
            liveData(context = viewModelScope.coroutineContext + Default) {
                if (prodAndCountList.isNullOrEmpty()) {
                    _state.postValue(ViewState.NO_PRODUCTS_IN_CART)
                    return@liveData
                }
                withContext(Main) { _state.value = ViewState.LOADING }
                val productsInCartList = getCartProductsListByIdsAndCount(prodAndCountList)
                emit(mutableListOf<KDiffUtilItem>().apply {
                    addAll(productsInCartList)
                    add(
                        TotalAndDeliveryPrice(
                            DELIVERY_PRICE,
                            productsInCartList.sumBy { it.totalPrice } + DELIVERY_PRICE)
                    )
                })
                _state.postValue(ViewState.DEFAULT)
            }
        }

    private suspend fun getCartProductsListByIdsAndCount(prodAndCountList: List<ProductIdAndCount>) =
        productsRepository.getProductListByIds(ids = prodAndCountList
            .map { productIdAndCount -> productIdAndCount.productId })
            .mapIndexed { index, product ->
                ProductInCart(
                    product,
                    prodAndCountList[index].count,
                    product.price * prodAndCountList[index].count
                )
            }

    fun addOneProductToCart(productId: Int) {
        viewModelScope.launch(IO) {
            userRepository.addProductToCart(productId, 1)
        }
    }

    fun removeOneProductFromCart(productId: Int) {
        viewModelScope.launch(IO) {
            userRepository.removeProductFromCart(productId, 1)
        }
    }

    enum class ViewState {
        DEFAULT,
        LOADING,
        NO_PRODUCTS_IN_CART
    }
}