package com.binarybeast.peekypoke.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.binarybeast.peekypoke.MainActivity
import com.binarybeast.peekypoke.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {


    companion object{
        const val TAG = "LoginFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToRegister: TextView = view.findViewById(R.id.go_to_register)

        goToRegister.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.auth_fragment_container, RegisterFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        val emailText: TextInputLayout = view.findViewById(R.id.email_text)
        val passwordText: TextInputLayout = view.findViewById(R.id.password_text)
        val loginButton : Button = view.findViewById(R.id.login_button)
        val loginProgress : ProgressBar = view.findViewById(R.id.login_progress)


        loginButton.setOnClickListener {
            val email = emailText.editText?.text.toString()
            val passsword = passwordText.editText?.text.toString()

            emailText.error = null
            passwordText.error = null

            if(TextUtils.isEmpty(email)){
                emailText.error = "Email is required"
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(passsword)){
                passwordText.error = "Password is required"
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailText.error = "Enter valid email address"
                return@setOnClickListener
            }

            loginProgress.visibility = View.VISIBLE
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,passsword)
                .addOnCompleteListener{ task ->
                    loginProgress.visibility = View.GONE

                    if(task.isSuccessful){
                        val intent = Intent(activity,MainActivity::class.java)
                        startActivity(intent)
                    } else{
                        Toast.makeText(context,"Something went wrong, Try again", Toast.LENGTH_LONG).show()
                        Log.d(TAG, task.exception.toString())

                    }
                    loginProgress.visibility = View.GONE

                }
        }
    }

}