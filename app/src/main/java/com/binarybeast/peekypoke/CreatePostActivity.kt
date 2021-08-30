package com.binarybeast.peekypoke

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreatePostActivity : AppCompatActivity() {
    private lateinit var selectImage: ImageView
    private  var imageUri: Uri? = null

    companion object{
        const val TAG = "CreatePostActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        selectImage = findViewById(R.id.select_image)
        val enterText : TextView = findViewById(R.id.enter_text)
        val postButton: Button = findViewById(R.id.post_button)


        selectImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080,1080)
                .start()
        }


        postButton.setOnClickListener {
            val text = enterText.text.toString()
            if(TextUtils.isEmpty(text)){
                Toast.makeText(this,"Description can not be empty",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            addPost(text)
        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(resultCode){
            Activity.RESULT_OK -> {
                val fileUri = data?.data
                selectImage.setImageURI(fileUri)
                imageUri = fileUri
            }
            ImagePicker.RESULT_ERROR ->{
                Toast.makeText(this,ImagePicker.getError(data),Toast.LENGTH_LONG).show()

            }
            else -> {
                Toast.makeText(this,"Task Cancelled", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun addPost(text: String) {

        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get()
            .addOnCompleteListener{
                val user = it.result?.toObject(User::class.java)

                val storage = FirebaseStorage.getInstance().reference.child("Images")
                    .child(FirebaseAuth.getInstance().currentUser?.email.toString() + "_" + System.currentTimeMillis() + ".jpg")

                val uploadTask = storage.putFile(imageUri!!) //Use let operateor

                uploadTask.continueWith{ task ->

                    if(!task.isSuccessful){
                        Log.d("upload Task",task.exception.toString())
                    }
                    storage.downloadUrl
                }.addOnCompleteListener{ urlTaskCompleted ->
                    if(urlTaskCompleted.isSuccessful){
                        val dowloadUri = urlTaskCompleted.result

                        val post = Post(text, dowloadUri.toString(),user!!, System.currentTimeMillis())

                        firestore.collection("Posts").document().set(post)
                            .addOnCompleteListener{ posted ->
                                if(posted.isSuccessful){
                                    Toast.makeText(this,"post successfully",Toast.LENGTH_LONG).show()
                                    finish()
                                }
                                else{
                                    Toast.makeText(this,"Error occured, Please try again",Toast.LENGTH_LONG).show()

                                }

                            }
                    }
                    else{
                        Toast.makeText(this,"post successfully",Toast.LENGTH_LONG).show()
                        Log.d(TAG , urlTaskCompleted.exception.toString())

                    }

                }
            }
    }
}