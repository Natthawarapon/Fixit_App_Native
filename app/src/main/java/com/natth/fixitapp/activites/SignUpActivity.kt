package com.natth.fixitapp.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.natth.fixitapp.R

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val actionbar = supportActionBar
        actionbar!!.title = "Sign Up"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp():Boolean {
        onBackPressed()

        return true
    }
}
