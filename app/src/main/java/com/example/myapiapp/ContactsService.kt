package com.example.myapiapp
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ContactsService {
    @GET("api/Contacts")
    fun getData(): Call<List<ContactItem>>
}