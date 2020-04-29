package com.natth.fixitapp.service

import android.service.autofill.FillEventHistory
import com.natth.fixitapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET(value = "technicians")
    fun getTech() : Call<List<Technician>>

    @GET(value = "reviewsbytech/{id}")
    fun getReviewsByTechnician(@Path("id") id:Int): Call<List<ReviewsByTechnician>>

    @POST(value = "requestFix")
    fun createRequestFix(@Body addRequestFixWithHistory :RequestFix):Call<HashMap<String,Int>>

    @GET(value = "historyFix/{id}")
    fun getHistoryRequestFix(@Path("id") id:Int):Call<List<RequestFix>>

    @POST(value = "change_status_fix" )
    fun changeStatusFix(@Body addHistoryFix:HistoryFix):Call<HashMap<String,String>>

    @GET("requestfixbyidFix/{id}")
    fun getrequestFixByID(@Path("id")id:Int):Call<List<DetailRequestFix>>

    @GET("requestfixbyUser/{id}")
    fun getrequestFixByUser(@Path("id")id:Int):Call<List<RequestFixByuser>>

    @GET(value = "IdFix/review/{id}")
    fun getReviewsByIdFix(@Path("id") id:Int): Call<List<ReviewByIdFix>>

    @POST(value = "review_store")
    fun createReviewFix(@Body addReview: Review ):Call<HashMap<String,String>>

    @GET("cancelFix/{id}")
    fun getCancelFixByUser(@Path("id")id:Int):Call<List<CauseCancelFix>>
}