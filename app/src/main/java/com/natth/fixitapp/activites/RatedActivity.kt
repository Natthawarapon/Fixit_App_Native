package com.natth.fixitapp.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.natth.fixitapp.R
import com.natth.fixitapp.adapter.ReviewAdapter
import com.natth.fixitapp.model.ReviewByIdFix
import com.natth.fixitapp.model.ReviewsByTechnician
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_rated.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode

class RatedActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)
    var idfix:Int? = 0
    var namestore:String? = null
    var dateFix:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rated)
        idfix  = intent.getStringExtra("idFixReview").toInt()
        namestore = intent.getStringExtra("namestore")
        dateFix = intent.getStringExtra("dateFix")

        tvNameStoreRated.text = "ร้าน : $namestore"
        tvIdFixRated.text = "รายการซ่อมที่ fx-${idfix.toString()}"
        tvDateTimeRated.text = "เมื่อเวลา : $dateFix "

        api.getReviewsByIdFix(idfix!!)
            .enqueue(object : Callback<List<ReviewByIdFix>> {
                override fun onResponse(
                    call: Call<List<ReviewByIdFix>>,
                    response: Response<List<ReviewByIdFix>>
                ) {
                for (i in 0..response.body()!!.size-1){
                  tvCommentRated.text = response.body()!![i].textReview
                    ratingBarRated.rating = (response.body()!![i].rating).toFloat()
                }

                    Log.d("RWT", "success ${response.body()}")


                }

                override fun onFailure(call: Call<List<ReviewByIdFix>>, t: Throwable) {
                    Log.d("RWT", "fail")
                }
            })
    }
}
