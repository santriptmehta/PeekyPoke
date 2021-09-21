package com.binarybeast.peekypoke

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.binarybeast.peekypoke.auth.AuthenticationActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null){
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }

        UserUtils.getCurrentUser()
        setFragment(FeedFragment())

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.feed_item ->{
                    setFragment(FeedFragment())
                }
                R.id.user_item ->{
//                    setFragment(FeedFragment)
                }
                R.id.chatroom_item ->{
//                    setFragment(FeedFragment)
                }
                R.id.profile_item ->{
//                    setFragment(FeedFragment)
                }
            }
            true
        }
    }




    private fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .commit()
    }
}