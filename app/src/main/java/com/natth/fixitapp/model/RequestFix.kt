package com.natth.fixitapp.model

data class RequestFix (

    var idUser:Int =0,
    var idTechnician:Int =0,
    var idRequest:Int = 0,
    var LastUpdate:String? =null,
    var status:String? = null ,
    var user_lat:String? = null,
    var user_lon :String? = null ,
    var user_address:String? = null


)
