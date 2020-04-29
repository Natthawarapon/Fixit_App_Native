package com.natth.fixitapp.activites

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.natth.fixitapp.R
import com.natth.fixitapp.model.DetailRequestFix
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.activity_detail_fix.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailFixActivity : AppCompatActivity() {

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var idFixDetail: String? =null
    var idFixClick:String? = null
    val api = retrofit.create(ApiService::class.java)
    var idFix:Int = 0
    val REQUEST_PHONE_CALL = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_fix)

        val actionbar = supportActionBar
        actionbar!!.title = "Detail Fix"
        actionbar.setDisplayHomeAsUpEnabled(true)


        idFixDetail = intent.getStringExtra("idFixx")
        idFixClick = intent.getStringExtra("id_fix")

        if (idFixDetail != null) {
            idFix = idFixDetail!!.toInt()
        }else {
            idFix = idFixClick!!.toInt()
        }



        api.getrequestFixByID(idFix).enqueue(object : Callback<List<DetailRequestFix>> {
            override fun onResponse(call: Call<List<DetailRequestFix>>, response: Response<List<DetailRequestFix>>) {
                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (response.body()!![i].idRequest == idFix) {
                        tvFixID.text = "รายการซ่อม:fx-${idFix}"
                        tvaddressStore.text = "จากร้าน :${response.body()!![i].nameStore}"
                        tvaddressUser.text ="ตำแหน่งของคุณ :${response.body()!![i].user_address}"
                        tvstatusFix.text = "รับซ่อมแล้ว"
                        tvDateFix.text = "เมื่อ : ${(response.body()!![i].lastUpdate).toString()}"
                    }
                }
            }
            override fun onFailure(call: Call<List<DetailRequestFix>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })

        btnChat.setOnClickListener {
            var idF = idFix.toString()
            val i = Intent(this, ChatActivity::class.java)
            i.putExtra("idFix_chat" , idF)
            startActivity(i)

        }

        btnCall.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            }else {

                startCall()
            }
        }
    }

    private fun startCall() {
        val phoneStore = "0993044146"
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: " +phoneStore)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            return
        }
        startActivity(callIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) {
            startCall()
        }
    }
    override fun onSupportNavigateUp():Boolean {
        onBackPressed()

        return true
    }


}
