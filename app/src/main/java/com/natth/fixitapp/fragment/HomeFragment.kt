package com.natth.fixitapp.fragment


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.computingforgeeks.fixitapp.DetailActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.natth.fixitapp.R
import com.natth.fixitapp.model.CauseCancelFix
import com.natth.fixitapp.model.Technician
import com.natth.fixitapp.service.ApiService
import kotlinx.android.synthetic.main.dialog_fragment_dismiss.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*


class HomeFragment : Fragment() , OnMapReadyCallback, PermissionListener, GoogleMap.OnInfoWindowClickListener {

    val REQUEST_CHECK_SETTINGS = 43


    var user_current_lat:Double = 0.0
    var user_current_lon:Double = 0.0
    var user_current_address:String? = null

    final val TASK_NAME_REQUEST_CODE =100
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    var markerStore:Marker? = null

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.39:9090/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(ApiService::class.java)

    var arrTechnicians = ArrayList<Technician>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.content_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //check
        val mapFragment:SupportMapFragment =this.childFragmentManager.findFragmentById(R.id.map)as SupportMapFragment
        mapFragment!!.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this.activity!!)
        api.getCancelFixByUser(id).enqueue(object : Callback<List<CauseCancelFix>> {
            override fun onResponse(call: Call<List<CauseCancelFix>>, response: Response<List<CauseCancelFix>>
            ) {

                Log.d("GetRequest", "success")
                for (i in 0..response.body()!!.size - 1) {
                    if (response.body()!![i].status == "dismiss") {
                        Log.d("aaa" , "${response.body()!!}")
                        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_dismiss, null)
                        mDialogView.dialogNameStore.text = "Pkservice"
                        mDialogView.dialogCause.text = "ไปไม่ทันเวลา"
                        val mBuilder = AlertDialog.Builder(context)
                            .setView(mDialogView).setCancelable(false)

                        val mAlertDialog = mBuilder.show()
                        mDialogView.btnClose.setOnClickListener {
                            mAlertDialog.dismiss()
                        }
//                        getCancelFix((response.body()!![i].name_store).toString())

                    }
                }

            }

            override fun onFailure(call: Call<List<CauseCancelFix>>, t: Throwable) {
                Log.d("GetRequest", "fail ${t}")
            }
        })

    }

     fun getCancelFix(name_store:String) {

         val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_dismiss, null)
         mDialogView.dialogNameStore.text = "${name_store}"
         mDialogView.dialogCause.text = "ไปไม่ทันเวลา"
         val mBuilder = AlertDialog.Builder(context)
             .setView(mDialogView).setCancelable(false)

         val mAlertDialog = mBuilder.show()
         mDialogView.btnClose.setOnClickListener {
             mAlertDialog.dismiss()
         }

    }


    override fun onMapReady(map: GoogleMap?) {


        this.activity
        googleMap = map?: return
        if (isPermissionGiven()){
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
            getLocationStore()
//            getCancelFix()

        } else {
            givePermission()
        }
        map?.let {
                googleMap = it
        }

    }
    private fun isPermissionGiven(): Boolean{
        return ActivityCompat.checkSelfPermission(context as Activity , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun givePermission() {
        Dexter.withActivity(context as Activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(this)
            .check()
    }
    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        getCurrentLocation()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        token!!.continuePermissionRequest()
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(context, "Permission required for showing location", Toast.LENGTH_LONG).show()
        this.activity!!.finish()
    }

    private fun getCurrentLocation() {

        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(context as Activity).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(context as Activity,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }
    fun getLocationStore(){

        googleMap.setOnInfoWindowClickListener(this)


        api.getTech().enqueue(object : Callback<List<Technician>> {
            override fun onResponse(
                call: Call<List<Technician>>,
                response: Response<List<Technician>>
            ) {

                for (i in 0..response.body()!!.size-1){
                    arrTechnicians.add(
                        Technician(
                            response.body()!![i].idTechnicians ,
                            response.body()!![i].nameStore ,
                            response.body()!![i].nameOwn ,
                            response.body()!![i].numberphone ,
                            response.body()!![i].email ,
                            response.body()!![i].latitude ,
                            response.body()!![i].longitude ,
                            null ,
                            response.body()!![i].address
                        )
                    )
                }

                Log.d("Tech", "success ${arrTechnicians}")
                for (i in 0..arrTechnicians.size-1){

                    var locationStore = LatLng(arrTechnicians[i].latitude!!.toDouble(),arrTechnicians[i].longitude!!.toDouble())
                    markerStore = googleMap.addMarker(
                        MarkerOptions()
                            .position(locationStore)
                            .title(arrTechnicians[i].nameStore)
                            .snippet(arrTechnicians[i].address)
                            .icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("boyicon",120,120)))
                    )


                }

            }

            override fun onFailure(call: Call<List<Technician>>, t: Throwable) {
                Log.d("Tech", "fail ${t}")
            }
        })

    }

    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnCompleteListener() { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result
                    user_current_lat = mLastLocation!!.latitude
                    user_current_lon = mLastLocation!!.longitude

                    var address = "No known address"

                    val gcd = Geocoder(context, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation!!.longitude, 1)

                        //set user location

                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(this.resources,
                            R.drawable.ic_pickup
                        ))
                    user_current_address = address
                    var marker1 =googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                            .title("Current Location")
                            .snippet(address)
                            .icon(icon)
                    )

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        .zoom(17f)
                        .build()
                    googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(context, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun resizeBitmap(drawableName: String, width: Int, height: Int): Bitmap {

        val imgBitmap = BitmapFactory.decodeResource(resources,resources.getIdentifier(drawableName,"drawable",this.activity!!.packageName))
        return Bitmap.createScaledBitmap(imgBitmap,width,height,false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onInfoWindowClick(infomarker: Marker?) {
        print(infomarker)
        var title =infomarker?.title
        var address = infomarker?.snippet
        print(title)
        var intent = Intent(this.activity, DetailActivity::class.java)
        intent.putExtra("nameStore",title)
        intent.putExtra("address" ,address)
        intent.putExtra("user_lat" , user_current_lat)
        intent.putExtra("user_lon" ,user_current_lon)
        intent.putExtra("user_current_address" ,user_current_address)

        startActivity(intent )
    }


}
