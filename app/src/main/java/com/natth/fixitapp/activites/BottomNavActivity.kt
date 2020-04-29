package com.natth.fixitapp.activites

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.fragment.app.ListFragment
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.natth.fixitapp.R
import com.natth.fixitapp.model.CauseCancelFix
import com.natth.fixitapp.model.HistoryFix
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.dialog_fragment_dismiss.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BottomNavActivity : AppCompatActivity() {

    private lateinit var mViewPager:ViewPager
    private lateinit var homeBtn:ImageButton
    private lateinit var listBtn:ImageButton
    private lateinit var notiBtn:ImageButton
    private lateinit var mPagerAdapter:PagerAdapter

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        // init views
        mViewPager = findViewById(R.id.mViewPager)
        homeBtn = findViewById(R.id.homeBtn)
        listBtn = findViewById(R.id.listBtn)
        notiBtn = findViewById(R.id.notiBtn)


        //onclick listner

        homeBtn.setOnClickListener {
            mViewPager.currentItem = 0

        }

        listBtn.setOnClickListener {

            mViewPager.currentItem = 1

        }

        notiBtn.setOnClickListener {
            mViewPager.currentItem = 2

        }

        mPagerAdapter = com.natth.fixitapp.adapter.PagerAdapter(supportFragmentManager)
        mViewPager.adapter = mPagerAdapter
        mViewPager.offscreenPageLimit = 3

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                changeTabs(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        var STOP_LOAD_REQUEST = 0
        var id:Int = 0
         val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                api.getCancelFixByUser(1).enqueue(object : Callback<List<CauseCancelFix>> {
                    override fun onResponse(call: Call<List<CauseCancelFix>>, response: Response<List<CauseCancelFix>>
                    ) {

                        Log.d("GetRequest", "success")
                        for (i in 0..response.body()!!.size - 1) {
                            if (response.body()!![i].status == "dismiss") {
                                Log.d("aaa" , "${response.body()!!}")
                                val mDialogView = LayoutInflater.from(this@BottomNavActivity).inflate(R.layout.dialog_fragment_dismiss, null)
                                mDialogView.dialogNameStore.text = "${response.body()!![i].name_store}"
                                id = response.body()!![i].idFix
                                mDialogView.tvIdFixC.text = "รายซ่อม : fx-${response.body()!![i].idFix}"
                                mDialogView.dialogCause.text = "ไปไม่ทันเวลา"
                                val mBuilder = AlertDialog.Builder(this@BottomNavActivity)
                                    .setView(mDialogView).setCancelable(false)

                                val mAlertDialog = mBuilder.show()

                                mDialogView.btnClose.setOnClickListener {
                                    mAlertDialog.dismiss()

                                    val addHistoryFix = HistoryFix()
                                    addHistoryFix.idRequest = id
                                    addHistoryFix.status = "dismissed"

                                    //4.request_fix and overtime  status == overtime
                                    api.changeStatusFix(addHistoryFix).enqueue(object : Callback<HashMap<String, String>> {
                                        override fun onResponse(call: Call<HashMap<String, String>>, response: Response<HashMap<String, String>>) {

                                            Log.d("postHistoryFix", "success")

                                        }
                                        override fun onFailure(call: Call<HashMap<String, String>>, t: Throwable) {
                                            Log.d("postHistoryFix", "fail ${t}")
                                        }
                                    })



                                }
//                        getCancelFix((response.body()!![i].name_store).toString())

                            }
                            if (STOP_LOAD_REQUEST == 1) break
                        }

                    }

                    override fun onFailure(call: Call<List<CauseCancelFix>>, t: Throwable) {
                        Log.d("GetRequest", "fail ${t}")
                    }
                })
                if (STOP_LOAD_REQUEST == 1) {
                    handler.removeCallbacks(this)
                } else {
                    handler.postDelayed(this, 1000)//1 sec delay
                }
            }
        }, 0)

        homeBtn.setImageResource(R.drawable.ic_home_pink)
        listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
        notiBtn.setImageResource(R.drawable.ic_person_black_24dp)
    }

    private fun changeTabs(position: Int) {

        if (position == 0) {
            homeBtn.setImageResource(R.drawable.ic_home_pink)
            listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_black_24dp)

        }
        if (position == 1) {
            homeBtn.setImageResource(R.drawable.ic_home_black_24dp)
            listBtn.setImageResource(R.drawable.ic_receipt_pink_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_black_24dp)
        }

        if (position == 2) {
            homeBtn.setImageResource(R.drawable.ic_home_black_24dp)
            listBtn.setImageResource(R.drawable.ic_receipt_black_24dp)
            notiBtn.setImageResource(R.drawable.ic_person_pink_24dp)
        }


    }



}
