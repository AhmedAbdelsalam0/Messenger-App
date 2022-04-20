package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import glide.GlideApp
import kotlinx.android.synthetic.main.activity_profile_.*
import java.io.ByteArrayOutputStream
import java.util.*


class Profile_Activity : AppCompatActivity() {


    private val storage : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val currentUserStorageRef : StorageReference
    get() = storage.reference.child(FirebaseAuth.getInstance().currentUser?.uid.toString())


    private val firestore : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocRef : DocumentReference
    get() = firestore.document("users/${FirebaseAuth.getInstance().currentUser?.uid.toString()}")

    private lateinit var userName : String
    private lateinit var userPic : String////////



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_)

        setSupportActionBar(toolbarProfile)
        supportActionBar?.title = "Me"
        supportActionBar?.setHomeButtonEnabled(true)  // idk why!!
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // to show the back button , but it wont work without the function onOptionsItemSelected

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  // M is sdk 23
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // it is to let status bar icons visible with the white color
        else
            window.statusBarColor = Color.WHITE  // to work with the smaller versions than sdk 23



        getUserInfo{ user ->

            userName = user.name

            Toast.makeText(this , user.picture , Toast.LENGTH_LONG).show()

            if (user.picture.isNotEmpty())
            {

                GlideApp.with(this@Profile_Activity)
                    .load(storage.getReference(user.picture))
                    .placeholder(R.drawable.icon_profile_pic)
                    .into(imgProfileProfileAct)

                Toast.makeText(this , "Done" , Toast.LENGTH_SHORT).show()

            }
            else
            {
                imgProfileProfileAct.setImageResource(R.drawable.icon_profile_pic)
            }


        }


        imgProfileProfileAct.setOnClickListener {

             val pictureIntent = Intent().apply {

                 type = "image/*"
                 action = Intent.ACTION_GET_CONTENT
                 putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))  // idk what does it mean

             }

            startActivityForResult(Intent.createChooser(pictureIntent, "Select Image"), 1)
            // create chooser is just to let you choose from many gallery apps

            // now you chose the picture , but didnt but it in the profile photo,,, you will do by the function onActivityResult


        }
        

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if condition is just to be ensure that all is right
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null)
        {
            imgProfileProfileAct.setImageURI(data.data)

            progProfile.visibility = View.VISIBLE

            // first , we want to compress the value of the selected image , we will use bitmap function
            val selectedImgBitmap = MediaStore.Images.Media.getBitmap(
                this.contentResolver,
                data.data
            )
            selectedImgBitmap.compress(Bitmap.CompressFormat.JPEG, 20, ByteArrayOutputStream())

            // now , we want to upload photo into server
            val selectedImgByte = ByteArrayOutputStream().toByteArray()
            // this variable is considered as the photo we want to upload to the server

            uploadProfileImage(selectedImgByte){ path ->

                val map = mutableMapOf< String , Any >()
                map["name"] = userName
                map["profilePicture"] = path

                currentUserDocRef.update(map)
            }

        }
        else
        {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }


    }


    private fun uploadProfileImage(selectedImgByte: ByteArray , onSuccess : (imgPath : String) -> Unit)
    {

        val ref = currentUserStorageRef.child("profilePictures/${UUID.nameUUIDFromBytes(selectedImgByte)}")  // any name , but this is to ensure that name is not repeated

        ref.putBytes(selectedImgByte).addOnCompleteListener {

            if (it.isSuccessful)
            {

                onSuccess(ref.path)
                progProfile.visibility = View.GONE


            }
            else
            {
                Toast.makeText(this@Profile_Activity , it.exception.toString() , Toast.LENGTH_LONG).show()
            }

        }



    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            android.R.id.home -> {
                finish(); return true
            }
        }

        return false
    }


    private fun getUserInfo(onComplete:(User) -> Unit) {

        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)
        }

    }



}