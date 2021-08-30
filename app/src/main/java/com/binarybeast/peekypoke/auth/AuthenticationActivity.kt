package com.binarybeast.peekypoke.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.binarybeast.peekypoke.R
import com.google.firebase.auth.FirebaseAuth

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)



        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container,LoginFragment())
            .commit()
    }
}