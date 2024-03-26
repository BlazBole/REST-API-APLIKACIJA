package com.example.myapiapp

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("posts")
    fun getApiData(): Call<List<MyDataItem>>
}