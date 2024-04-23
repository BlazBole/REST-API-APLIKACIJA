package com.example.myapiapp
import android.view.textclassifier.ConversationActions.Request
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    @GET("api/Users/usernameById/{userId}")
    fun getUsernameByUserId(@Path("userId") userId: Int): Call<ContactItem>

    @POST("api/Users/login")
    fun loginUser(@Body loginRequest: ContactItem): Call<ResponseBody>

    @PUT("api/Users/{id}")
    fun updateUser(@Path("id") id: Int, @Body updateUserRequest: ContactItem): Call<Void>

    @POST("api/Users/Inventory/AddToInventory")
    fun addToInventory(@Body addToInventoryRequest: InventoryItem): Call<InventoryItem>

    @GET("api/Users/Inventory/GetInventory")
    fun getInventory(): Call<List<InventoryItem>>

    @GET("api/Users/Inventory/GetInventoryByUser/{userId}")
    fun getInventoryByUser(@Path("userId") userId: Int): Call<List<InventoryItem>>

    @DELETE("api/Users/Inventory/DeleteInventory/{id}")
    fun deleteInventory(@Path("id") id: Int): Call<Void>
}