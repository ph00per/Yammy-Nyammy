package com.phooper.yammynyammy.data.api

import com.phooper.yammynyammy.data.models.Product
import retrofit2.http.GET
import retrofit2.http.Query

interface ShopApi {

    @GET("ph00per/Fake-json-server-for-Yammy-Nyammy/goods")
    suspend fun getProductListByCategory(@Query("category") category: String): List<Product>


}