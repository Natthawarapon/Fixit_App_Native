package com.natth.fixitapp.activites

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.natth.fixitapp.BuildConfig
import com.natth.fixitapp.R
import com.natth.fixitapp.adapter.ChatAdapter
import com.natth.fixitapp.model.Chat
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ChatActivity : AppCompatActivity() {
    val db = Firebase.firestore
   val  storage = Firebase.storage
    val storageRef = storage.reference

    // Create a reference to "mountains.jpg"
    var docIdByFixId :String? = null
    var docByFixIdcash :String? = null
    var idFixDetail: String? =null
    var idFixClick:String? = null
    val listarray =arrayListOf<Chat>()
    var nameImage:String? =null

    private lateinit var mCurrentPhotoPath: String

    private  val IMAGE_CAPTURE_CODE = 1001
    private  val PERMISSION_CODE = 100

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAv97pifQ:APA91bH5W3yCWTjp-C87O5-PD2EPbDMN6IqvwABq0-VtXDT8LnLSe-_E2W6DWDqOs7cZnysXDwnjyKUjaSe_LFzXtSO1uKBvj4UJ4VHkRleMTACqTSTcLmYxSQNc53eVUE2MW0dXR4F4"
    private val contentType = "application/json"
    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
//        val mountainImagesRef = storageRef.child("res/drawable/boyicon.png")
        idFixDetail = intent.getStringExtra("idFix_chat")
        idFixClick = intent.getStringExtra("id_fix_chat")

        if (idFixDetail != null) {
            docByFixIdcash = idFixDetail
        }else {
            docByFixIdcash = idFixClick
        }
        var x :String = "184"
        docByFixIdcash  =x
        loadMessage(docByFixIdcash!!)

//        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_chat)
//        FirebaseMessaging.getInstance().subscribeToTopic("/test")
//
//        binding.submit.setOnClickListener {
//            if (!TextUtils.isEmpty(binding.msg.text)) {
//                val topic = "eCMWnGfHHos:APA91bHM6PPeY0dmNrQOnjTzslq78mfI2PMUfSsrCB46FqZcIuAY9-0WgjJewgQQpSLfNmyFnKmcKIqJew6_C5l8g1Ib8ZiBAX22ImpCp4DChzbxU4fCQKULMZP8S2cjRjtQBdtAjOJq" //topic has to match what the receiver subscribed to
//
//                val notification = JSONObject()
//                val notifcationBody = JSONObject()
//
//                try {
//                    notifcationBody.put("title", "Firebase Notification")
//                    notifcationBody.put("message", binding.msg.text)
//                    notification.put("to", topic)
//                    notification.put("data", notifcationBody)
//
//                    Log.e("TAG", "try")
//                } catch (e: JSONException) {
//                    Log.e("TAG", "onCreate: " + e.message)
//                }
//
//                sendNotification(notification)
//            }
//        }

        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnSuccessListener(this, { instanceIdResult ->
                val newToken: String = instanceIdResult.getToken()

                Log.e("newToken", newToken)
            })
        imageView_send.setOnClickListener {
            var text = editText_message.text.toString()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
          val  dateTime = current.format(formatter)


            val docref = db.collection("message")


            if (text.isNotEmpty() ) {
                if (docByFixIdcash != null) {
                    val messages = hashMapOf(
                        "who" to "user" ,
                        "msg" to text ,
                        "dateTime" to dateTime ,
                        "type" to "Message"
                    )
                    val docRef = db.collection("messages").document(docByFixIdcash!!).collection("msg")
                    docRef.add(messages)
                        .addOnSuccessListener { documentReference ->
                            Log.d("TAG", "DocumentSnapshot successfully written!")
                            editText_message.getText()?.clear();
                        }
                        .addOnFailureListener { e ->
                            Log.w("TAG", "Error adding document", e)
                        }

                } else {
                    Toast.makeText(this, "การส่งข้อความไม่สำเร้จ", Toast.LENGTH_LONG).show()
                }




                    val topic = "eCMWnGfHHos:APA91bHM6PPeY0dmNrQOnjTzslq78mfI2PMUfSsrCB46FqZcIuAY9-0WgjJewgQQpSLfNmyFnKmcKIqJew6_C5l8g1Ib8ZiBAX22ImpCp4DChzbxU4fCQKULMZP8S2cjRjtQBdtAjOJq" //topic has to match what the receiver subscribed to

                    val notification = JSONObject()
                    val notifcationBody = JSONObject()

                    try {
                        notifcationBody.put("title", "There are new messages !!")
                        notifcationBody.put("message", text)
                        notification.put("to", topic)
                        notification.put("data", notifcationBody)

                        Log.e("TAG", "try")
                    } catch (e: JSONException) {
                        Log.e("TAG", "onCreate: " + e.message)
                    }

                    sendNotification(notification)

            }else{
                Toast.makeText(this, "กรุณาพิมพ์ข้อความคะ ", Toast.LENGTH_LONG).show()
            }

        }

        fab_send_image.setOnClickListener {

            if (checkSelfPermission(android.Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
            var permission = arrayOf(android.Manifest.permission.CAMERA ,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission ,PERMISSION_CODE)
            }else{
                openCamera()
            }


        }
    }
    private fun sendNotification(notification: JSONObject) {
        Log.e("TAG", "sendNotification")
        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                Log.i("TAG", "onResponse: $response")
//                msg.setText("")
            }, Response.ErrorListener  {
                Toast.makeText(this@ChatActivity, "Request error", Toast.LENGTH_LONG).show()
                Log.i("TAG", "onErrorResponse: Didn't work")
            }) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        nameImage = imageFileName
        val storageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "Camera"
        )
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.absolutePath
        return image
    }
    fun openCamera() {
        var Filephoto: File? = null
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Filephoto = createImageFile()
        if (Filephoto != null) {
            val photoURI = FileProvider.getUriForFile(
                this@ChatActivity,
                BuildConfig.APPLICATION_ID + ".provider",
                createImageFile()!!
            )

            Toast.makeText(this ,"$photoURI" ,Toast.LENGTH_LONG).show()
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val  dateTime1 = current.format(formatter)

        if (requestCode == IMAGE_CAPTURE_CODE){
        if (resultCode == Activity.RESULT_OK){
            val imageUri = Uri.parse(mCurrentPhotoPath)
            val file = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(file)
                val stream = FileInputStream(file)
                val ImageRef = storageRef.child("$docByFixIdcash/$nameImage.jpg")
             var  uploadTask = ImageRef.putStream(stream)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                }.addOnSuccessListener {
                    val result = it.metadata!!.reference!!.downloadUrl;
                    result.addOnSuccessListener {

                        val imageLink = it.toString()
                        if (docByFixIdcash != null) {
                            val messages = hashMapOf(
                                "who" to "user" ,
                                "msg" to imageLink ,
                                "dateTime" to dateTime1,
                                "type" to "Image"
                            )
                            val docRef = db.collection("messages").document(docByFixIdcash!!).collection("msg")
                            docRef.add(messages)
                                .addOnSuccessListener { documentReference ->
                                    Log.d("TAG", "DocumentSnapshot successfully written!")
                                    editText_message.getText()?.clear();
                                }
                                .addOnFailureListener { e ->
                                    Log.w("TAG", "Error adding document", e)
                                }

                        }
                        Toast.makeText(this ,"Upload $nameImage.jpg Successful" ,Toast.LENGTH_LONG).show()
//                        Toast.makeText(this ,"$imageLink" ,Toast.LENGTH_LONG).show()


                    }
                }

              var x = BitmapFactory.decodeStream(ims)

            } catch (e: FileNotFoundException) {
                return e.printStackTrace()
            }
            MediaScannerConnection.scanFile(this@ChatActivity,
                arrayOf(imageUri.path),
                null
            ) { path, uri ->

            }
        }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }else{

                    Toast.makeText(this , "Permission denied" ,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    fun  loadMessage(id:String) {

        val doc = db.collection("messages").document(id!!).collection("msg").orderBy("dateTime")
        doc.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("TAG", "listen:error", e)
                return@addSnapshotListener
            }

            for (dc in snapshots!!.documentChanges) {
                    val c = Chat()
                var who :String
                var img :String
                when (dc.type) {
                    DocumentChange.Type.ADDED -> {
//                        if (dc.document.data.getValue("type") == "Image"){
//                            Glide.with(this).load(dc.document.data.getValue("msg"))
//
//                        }
                        c.msg = dc.document.data.getValue("msg") as String?
                        c.dateTime = dc.document.data.getValue("dateTime") as String?
                        c.who = dc.document.data.getValue("who") as String?
                        c.type = dc.document.data.getValue("type") as String
                        who = dc.document.data.getValue("who") as String

                        listarray.add(c)
                        recycler_view_message.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = ChatAdapter(listarray )
                        }
                        Log.d("TAG1", "New city: ${dc.document.data}")
                    }
                }
            }
        }

    }



}
