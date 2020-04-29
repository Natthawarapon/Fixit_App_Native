package com.natth.fixitapp.model

data class RequestFixByuser (
    var    idUser:Int =0 ,
    var nameStore:String? = null,
    var idRequest:Int = 0,
    var status:String? = null,
    var lastUpdate:String? = null,
    var firstnameUser:String? = null,
    var lastnameUser:String? = null,
    var user_lat :String? = null,
    var user_lon :String? = null,
    var tech_lat:String? = null,
    var tech_lon :String? = null,
    var   user_address:String? = null

)