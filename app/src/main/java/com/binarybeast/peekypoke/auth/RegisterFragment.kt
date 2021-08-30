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
import com.google.firebase.firestore.FirebaseFirestore
import com.binarybeast.peekypoke.User

class RegisterFragment : Fragment() {



    companion object{
        const val TAG = "RegisterFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val go_to_login: TextView = view.findViewById(R.id.go_to_login)

        go_to_login.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.auth_fragment_container, LoginFragment())
                ?.addToBackStack(null)
                ?.commit()
        }


        val emailText: TextInputLayout = view.findViewById(R.id.email_text)
        val nametext: TextInputLayout = view.findViewById(R.id.name_text)
        val passwordText: TextInputLayout = view.findViewById(R.id.password_text)
        val confirmPasswordText: TextInputLayout = view.findViewById(R.id.confirm_password_text)
        val registerButton: Button = view.findViewById(R.id.register_button)
        val registerProgress: ProgressBar = view.findViewById(R.id.register_progress)



        registerButton.setOnClickListener {

            val email = emailText.editText?.text.toString()
            val name = nametext.editText?.text.toString()
            val passsword = passwordText.editText?.text.toString()
            val confirm_passsword = confirmPasswordText.editText?.text.toString()

            emailText.error = null
            nametext.error = null
            passwordText.error = null
            confirmPasswordText.error = null

            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(name)) {
                emailText.error = "Name is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passsword)) {
                passwordText.error = "Password is required"
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(confirm_passsword)) {
                passwordText.error = "Password is required"
                return@setOnClickListener
            }
            if (passsword != confirm_passsword) {
                confirmPasswordText.error = "Password does not match"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Enter valid email address"
                return@setOnClickListener
            }

            registerProgress.visibility = View.VISIBLE


            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, passsword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(auth.currentUser?.uid!!, name, email)
                        val firestore = FirebaseFirestore.getInstance().collection("Users")
                        firestore.document(auth.currentUser?.uid!!).set(user)
                            .addOnCompleteListener{ task2 ->
                                registerProgress.visibility = View.GONE
                                if(task2.isSuccessful) {
                                    val intent = Intent(activity, MainActivity::class.java)
                                    startActivity(intent)
                                }
                                else{
                                    Toast.makeText(context,"Something went wrong", Toast.LENGTH_LONG).show()
                                    Log.d(TAG, task.exception.toString())

                                }

                            }
                    } else {
                        registerProgress.visibility = View.GONE
                        Toast.makeText(context,"Something went wrong, Try again", Toast.LENGTH_LONG).show()
                        Log.d(TAG, task.exception.toString())

                    }
                }

        }
    }

}