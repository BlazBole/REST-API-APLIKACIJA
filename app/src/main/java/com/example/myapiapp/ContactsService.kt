package com.example.myapiapp
import android.view.textclassifier.ConversationActions.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContactsService {
    @GET("api/Users")
    fun getData(): Call<List<ContactItem>>

    @POST("api/Users")
    fun addUser(@Body addUserRequest: ContactItem
    ): Call<ContactItem>

    @GET("api/Users/email/{email}")
    fun getUserByEmail(@Path("email") email: String): Call<ContactItem>

    @GET("api/Users/username/{username}")
    fun getUserByUsername(@Path("username") username: String): Call<ContactItem>

    @POST("api/Users/login")
    fun loginUser(@Body loginRequest: ContactItem): Call<ResponseBody>

}