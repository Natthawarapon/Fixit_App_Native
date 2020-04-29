package com.natth.fixitapp.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.natth.fixitapp.R
import com.natth.fixitapp.adapter.CurrentListAdapter
import com.natth.fixitapp.adapter.PreviousListAdapter
import com.natth.fixitapp.model.RequestFixByuser
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.activity_previous_list.*
import kotlinx.android.synthetic.main.fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PreviousListActivity : AppCompatActivity() {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()
    val api = retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_list)



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
                    if (response.body()!![i].status != "accept") {
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

                        Prelist.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = PreviousListAdapter(listarray)
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

    }
}
