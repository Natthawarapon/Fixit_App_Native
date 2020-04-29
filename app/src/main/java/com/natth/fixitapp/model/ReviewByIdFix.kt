package com.natth.fixitapp.model

data class ReviewByIdFix(
    var idRequest: Int = 0,
    var textReview: String? = null,
    var rating: Int = 0,
    var lastUpdate: String? = null,
    var firstNameUser: String? = null,
    var lastNameUser: String? = null,
    var nameStore: String? = null

)