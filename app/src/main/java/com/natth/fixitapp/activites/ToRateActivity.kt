package com.natth.fixitapp.activites

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import com.google.gson.internal.ObjectConstructor
import com.natth.fixitapp.R
import com.natth.fixitapp.model.Review
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.activity_to_rate.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ToRateActivity : AppCompatActivity() {

    companion object {
        val TAG = ToRateActivity::class.java.simpleName
    }
    lateinit var ratingBarStore: RatingBar
    var nameStoreReview :String? = null
    var dateTime:String? =null
    var idFixDone:String? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_rate)
        nameStoreReview = intent.getStringExtra("namestore")
        idFixDone = intent.getStringExtra("idFixDone")
        dateTime = intent.getStringExtra("dateFix")
        tvNamestoreReview.text = "ร้าน : $nameStoreReview"
        tvFixIdReview.text = "รายการซ่อม fx-$idFixDone"
        tvDateFixReview.text = "เมื่อเวลา : $dateTime"

        ratingBarStore = findViewById(R.id.ratingBarStore)
        var ratingStore :Float? = null
        ratingBarStore.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingStore = rating
            Log.d(TAG, "$rating")
            if (rating <= 2f){
                ratingBar.progressDrawable.setTint(Color.parseColor("#FF5733"))
            }else if (rating > 2f && rating <= 3f){
                ratingBar.progressDrawable.setTint(Color.parseColor("#FF8333"))
            }else if (rating > 3f && rating <= 4f ){
                ratingBar.progressDrawable.setTint(Color.parseColor("#FFAB33"))
            }else{
                ratingBar.progressDrawable.setTint(Color.parseColor("#FFC107"))
            }
        }

        btnReview.setOnClickListener {
        var Rat = ratingStore!!.toFloat()
            var Comment = etComment.text.toString()
            if (Rat > 0.0 && Comment.isNotEmpty()){


                //API ADD Review

                    val addReview = Review()
                addReview.idRequest = idFixDone!!.toInt()
                addReview.textreview = Comment
                addReview.rating = Rat.toInt()
                addReview.status = "review"




                api.createReviewFix(addReview).enqueue(object : Callback<HashMap<String,String>> {
                    override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {

                        Log.d("postFixss", "successff")

                        Toast.makeText(this@ToRateActivity,"ให้คะแนนเรียบร้อยแล้ว" ,Toast.LENGTH_LONG).show()
                        val refresh = Intent(this@ToRateActivity, PreviousListActivity::class.java)
                        startActivity(refresh)
                        finish() //
                    }
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        Log.d("postFix", "fail ${t}")
                    }

                })

            }else{

                    Toast.makeText(this ,"กรุณาให้คะแนนและใส่คอมเมนต์คะ" ,Toast.LENGTH_LONG).show()


            }


        }
    }
}
