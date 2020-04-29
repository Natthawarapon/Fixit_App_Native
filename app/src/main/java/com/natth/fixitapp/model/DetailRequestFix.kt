package com.natth.fixitapp.model

data class DetailRequestFix (

    var idTechnician:Int =0,
    var nameStore:String? =null ,
    var idUser:Int =0,
    var firstnameUser:String? =null ,
    var lastnameUser:String?=null,
    var idRequest:Int = 0,
    var lastUpdate:String? =null,
    var status:String? = null,
    var user_lat :String? = null,
    var user_lon:String? = null ,
    var tech_lat:String? = null ,
    var tech_lon:String? = null ,
    var user_address:String? = null

)