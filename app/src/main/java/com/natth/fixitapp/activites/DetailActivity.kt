package com.computingforgeeks.fixitapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.natth.fixitapp.R
import com.natth.fixitapp.activites.DetailFixActivity
import com.natth.fixitapp.adapter.ReviewAdapter
import com.natth.fixitapp.model.*
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.dialog_fragment.view.*
import kotlinx.android.synthetic.main.dialog_fragment_dismiss.view.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import kotlin.concurrent.timer
import kotlin.math.log

class DetailActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)
    var idstore: Int = 0
    var idfix: Int = 0
    var user_lat:String? = null
    var user_lon:String? = null
    var  user_current_address :String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var nameStore: String = intent.getStringExtra("nameStore")
        var addressStore: String = intent.getStringExtra("address")
         user_current_address  = intent.getStringExtra("user_current_address")
        user_lat = intent.getDoubleExtra("user_lat",0.0).toString()
        user_lon = intent.getDoubleExtra("user_lon" ,0.0).toString()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        iv1.setImageResource(R.drawable.sing)


        tvNamestore.text = nameStore
        tvAddress.text = addressStore
//        ivProfile.setImageResource(R.drawable.profile)
        ivProfileCover.setImageResource(R.drawable.profilecover)

        api.getTech().enqueue(object : Callback<List<Technician>> {
            override fun onResponse(
                call: Call<List<Technician>>,
                response: Response<List<Technician>>
            ) {

                for (i in 0..response.body()!!.size - 1) {
                    if (nameStore == response.body()!![i].nameStore) {
                        var idtech: Int = response.body()!![i].idTechnicians
                        idstore = idtech
                        loadReview(idtech)

                    }
                }
            }

            override fun onFailure(call: Call<List<Technician>>, t: Throwable) {
                Log.d("Tech", "fail ${t}")
            }
        })

        btnNavigator.setOnClickListener {


        }

        getCancelFix()

    }

    private fun loadReview(idtechnical: Int) {
        var ratingTotal: Int = 0
        api.getReviewsByTechnician(idtechnical)
            .enqueue(object : Callback<List<ReviewsByTechnician>> {
                override fun onResponse(
                    call: Call<List<ReviewsByTechnician>>,
                    response: Response<List<ReviewsByTechnician>>
                ) {
                    Log.d("RWT", "success")
                    rcvReviews.apply {
                        layoutManager = LinearLayoutManager(this@DetailActivity)
                        adapter = ReviewAdapter(response.body()!!)

                    }

                    for (i in 0..response.body()!!.size - 1) {
                        ratingTotal += response.body()!![i].rating
                    }
                    if (ratingTotal > 0) {
                        var x = ratingTotal
                        var y = response.body()!!.size
                        var totalrating: Float = x.toFloat() / y.toFloat()
                        var ratingDecimal =
                            totalrating.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toFloat()
                        ratingBarTotal.rating = totalrating
                        tvratingTotal.text = ratingDecimal.toString() + "/5"
                        tvCountreview.text = (response.body()!!.size).toString() + " review"
                        tvReview.text = "Review(${y})"
                    }


                }

                override fun onFailure(call: Call<List<ReviewsByTechnician>>, t: Throwable) {
                    Log.d("RWT", "fail")
                }
            })

    }

    fun requestFix(view: View) {

        btnRequest.isEnabled = false
        val mAlert = AlertDialog.Builder(this@DetailActivity)
        mAlert.setCancelable(false)
        mAlert.setTitle("คุณต้องการเรียกซ่อม !")
        mAlert.setMessage("คุณต้องการเรียกซ่อมจากร้าน ${tvNamestore.text} ใช่หรือไม่")

        mAlert.setPositiveButton("Yes") { dialog, id ->
            val addRequestFixWithHistory = RequestFix()
            addRequestFixWithHistory.idUser = 1
            addRequestFixWithHistory.idTechnician = idstore
            addRequestFixWithHistory.idRequest = 0
            addRequestFixWithHistory.LastUpdate = null
            addRequestFixWithHistory.status = "request"
            addRequestFixWithHistory.user_lat = user_lat
            addRequestFixWithHistory.user_lon = user_lon
            addRequestFixWithHistory.user_address = user_current_address

            api.createRequestFix(addRequestFixWithHistory).enqueue(object : Callback<HashMap<String, Int>> {

                    override fun onResponse(call: Call<HashMap<String, Int>>, response: Response<HashMap<String, Int>>) {
                        Log.d("postFix", "success")

                        var idRF = response.body()!!.get("idRequestFix")
                        idfix = idRF!!.toInt()
                        idRF!!.toInt()
                        waitToReplyFix(idRF)
//                    checkStatus(idRF)
                    }

                    override fun onFailure(call: Call<HashMap<String, Int>>, t: Throwable) {

                        Log.d("postFix", "fail ${t}")


                    }

                })


        }

        mAlert.setNegativeButton("No") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
        }

        mAlert.show()


    }


    fun waitToReplyFix(id: Int) {

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_fragment, null)
        mDialogView.dialogTitle.text = "รายการเรียกซ่อม !"
        mDialogView.dialogMessage.text = "กรุณารอการตอบรับจากร้าน ${tvNamestore.text} สักครู่"
        mDialogView.btncancel.text = "ยกเลิกการเรียกซ่อม"
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView).setCancelable(false)

        val mAlertDialog = mBuilder.show()

        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mDialogView.dialogTime.text = " ${millisUntilFinished / 1000}"

            }

            override fun onFinish() {
                mAlertDialog.dismiss()
                var mgs = "ขออภัย การทำรายการเกินกว่าเวลาที่กำหนด"
                overTimeRequestFix(mgs)
                val addHistoryFix = HistoryFix()
                addHistoryFix.idRequest = id
                addHistoryFix.status = "overtime"

                //4.request_fix and overtime  status == overtime
                api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                    override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {
                        btnRequest.isEnabled = true
                        Log.d("postHistoryFix", "success")
                    }
                    override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                        Log.d("postHistoryFix", "fail ${t}")
                    }
                })
            }
        }
        timer.start()

        mDialogView.btncancel.setOnClickListener {
            timer.cancel()
            mAlertDialog.dismiss()
            var mgs = "คุณได้ยกเลิกการเรียกซ่อมจากร้าน ${tvNamestore.text} เรียบร้อยแล้ว"
            val addHistoryFix = HistoryFix()
            addHistoryFix.idRequest = id
            addHistoryFix.status = "cancel"

            //3. user cancel request_fix  status == cancel
            api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {
                    btnRequest.isEnabled = true
                    Log.d("postHistoryFix", "success")
                }
                override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                    Log.d("postHistoryFix", "fail ${t}")
                }
            })
            overTimeRequestFix(mgs)
            //status == cancel

        }

        var x = 0
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Call your function here
                api.getHistoryRequestFix(id).enqueue(object : Callback<List<RequestFix>> {
                    override fun onResponse(call: Call<List<RequestFix>>, response: Response<List<RequestFix>>) {
                        Log.d("postTech", "success")
                        var count = response.body()!!.size - 1
                        for (i in 0..count) {
                            if (response.body()!![i].idRequest == id) {
                                //Check Status responseFix
                                //1.response status == accept call acceptRequestFix()
                                if (response.body()!![i].status == "accept") {
                                    x = 1
                                    timer.cancel()
                                    mAlertDialog.dismiss()
                                    acceptRequestFix(id)
                                    btnRequest.isEnabled = false
                                }
                                //Check Status responseFix
                                //2.response status == reject rejectRequestFix()
                                else if (response.body()!![i].status == "reject") {
                                    x = 1
                                    timer.cancel()
                                    mAlertDialog.dismiss()
                                    rejectRequestFix()
                                    btnRequest.isEnabled = true

                                } else if (response.body()!![i].status == "done"){
                                    btnRequest.isEnabled = true
                                }
                            }
                            if (x==1) break
                        }
                    }
                    override fun onFailure(call: Call<List<RequestFix>>, t: Throwable) {
                        Log.d("postTech", "fail ${t}")
                    }
                })
                if (x == 1) {
                    handler.removeCallbacks(this)
                } else {
                  handler.postDelayed(this, 1000)//1 sec delay

                }

            }

        }, 0)
    }

    fun getCancelFix() {

        api.getCancelFixByUser(1).enqueue(object : Callback<List<CauseCancelFix>> {
            override fun onResponse(call: Call<List<CauseCancelFix>>, response: Response<List<CauseCancelFix>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (response.body()!![i].status == "dismiss") {
                        Log.d("list99" , "${response.body()!!}")
                        val mDialogView = LayoutInflater.from(this@DetailActivity).inflate(R.layout.dialog_fragment_dismiss, null)
                        mDialogView.dialogNameStore.text = "${response.body()!![i].name_store}"
                        mDialogView.tvIdFixC.text = "รายการซ่อมที่ :fx-${response.body()!![i].idFix}"
                        mDialogView.dialogCause.text = "ไปไม่ทันเวลา"
                        val mBuilder = AlertDialog.Builder(this@DetailActivity)
                            .setView(mDialogView).setCancelable(false)

                        val mAlertDialog = mBuilder.show()
                        mDialogView.btnClose.setOnClickListener {
                            mAlertDialog.dismiss()
                        }


                    }
                }

            }

            override fun onFailure(call: Call<List<CauseCancelFix>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })

    }

    fun overTimeRequestFix(msg: String) {
        val mAlert = AlertDialog.Builder(this@DetailActivity)
        mAlert.setCancelable(false)
        if (msg == "คุณได้ยกเลิกการเรียกซ่อมจากร้าน ${tvNamestore.text}") {
            mAlert.setTitle("คุณยกเลิกการใช้บริการ !")
        } else {
            mAlert.setTitle("รายการเรียกซ่อมถูกยกเลิก !")
        }
        mAlert.setMessage("${msg} ")

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
        }
        mAlert.show()
    }

    fun rejectRequestFix() {

        val mAlert = AlertDialog.Builder(this@DetailActivity)
        mAlert.setCancelable(false)
        mAlert.setTitle("รายการเรียกซ่อมถูกปธิเสธ !")
        mAlert.setMessage("ขออภัย ขณะนี้ร้าน ${tvNamestore.text} ไม่พร้อมให้บริการ")

        mAlert.setNegativeButton("Close") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
        }
        mAlert.show()

    }

    fun acceptRequestFix(idFix:Int) {
        var x = idFix.toString()
        val mAlert = AlertDialog.Builder(this@DetailActivity)
        mAlert.setCancelable(false)
        mAlert.setTitle("ยอมรับรายการเรียกซ่อม !")
        mAlert.setMessage("รายการเรียกซ่อมของคุณถูกยอมรับแล้ว ขณะนี้ร้าน ${tvNamestore.text} กำลังมาคะ")
        mAlert.setPositiveButton("OK") { dialog: DialogInterface?, which: Int ->
            dialog!!.dismiss()
            //Go to current list fix
            val i = Intent(this, DetailFixActivity::class.java)
            i.putExtra("idFixx" , x)
            startActivity(i)


        }

        mAlert.show()
    }




}
