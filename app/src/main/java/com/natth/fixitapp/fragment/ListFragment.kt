package com.natth.fixitapp.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.natth.fixitapp.R
import com.natth.fixitapp.activites.PreviousListActivity
import com.natth.fixitapp.adapter.CurrentListAdapter
import com.natth.fixitapp.model.RequestFixByuser
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * A simple [Fragment] subclass.
 */
class ListFragment : Fragment() {


    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)
    val REQUEST_PHONE_CALL = 1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        var id = 1
        val listarray =arrayListOf<RequestFixByuser>()
        api.getrequestFixByUser(id).enqueue(object : Callback<List<RequestFixByuser>> {
            override fun onResponse(call: Call<List<RequestFixByuser>>, response: Response<List<RequestFixByuser>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (response.body()!![i].status == "accept") {
                        Log.d("list99" , "${response.body()!!}")
                        val rqu= RequestFixByuser()
                        rqu.idUser = response.body()!![i].idUser
                        rqu.nameStore = response.body()!![i].nameStore
                        rqu.idUser = response.body()!![i].idUser
                        rqu.firstnameUser = response.body()!![i].firstnameUser
                        rqu.lastnameUser = response.body()!![i].lastnameUser
                        rqu.idRequest =  response.body()!![i].idRequest
                        rqu.lastUpdate = response.body()!![i].lastUpdate
                        rqu.status = response.body()!![i].status
                        rqu.user_lat = response.body()!![i].user_lat
                        rqu.user_lon = response.body()!![i].user_lon
                        rqu.tech_lat = response.body()!![i].tech_lat
                        rqu.tech_lon = response.body()!![i].tech_lon
                        rqu.user_address = response.body()!![i].user_address

                        listarray.add(rqu)

                        list.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = CurrentListAdapter(listarray) {
                                if(ActivityCompat.checkSelfPermission(context , Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
                            }else {

                                startCall()
                            }
                            }


                        }

                    }
                }
                for (item in  listarray){
                    Log.d("list88" , "${item}")
                }

            }

            override fun onFailure(call: Call<List<RequestFixByuser>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })

        oldList.setOnClickListener {
            val i = Intent(activity, PreviousListActivity::class.java)
            startActivity(i)
        }


    }
    private fun startCall() {
        val phoneStore = "0901115099"
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel: " +phoneStore)
        if (ActivityCompat.checkSelfPermission(
                context!!,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.CALL_PHONE),REQUEST_PHONE_CALL)
            return
        }
        startActivity(callIntent)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PHONE_CALL) {
            startCall()
        }
    }
}


