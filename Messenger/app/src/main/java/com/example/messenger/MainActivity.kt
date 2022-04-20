package com.example.messenger

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.messenger.fragments.Chats_Fragment
import com.example.messenger.fragments.More_Fragment
import com.example.messenger.fragments.People_Fragment
import com.example.messenger.model.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import glide.GlideApp
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{

    private val mAuthMain : FirebaseAuth by lazy {  // it is short way to initialize
        FirebaseAuth.getInstance()
    }

    private val storage : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }

    private val firestore : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }


    private val mChats_Fragment = Chats_Fragment()
    private val mPeople_Fragment = People_Fragment()
    private val mMore_Fragment = More_Fragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        funStatusBar()  // to let status bar better



        bottomNavView1.setOnNavigationItemSelectedListener(this@MainActivity)  // it is a listener for the fun " onNavigationItemSelected "
        funSetFragment(mChats_Fragment) // to start the app with chats fragment



        try {
            firestore.collection("users")
                .document(mAuthMain.currentUser?.uid.toString())
                .get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)

                    if (user!!.picture.isNotEmpty())
                    {
                        GlideApp.with(this@MainActivity)
                            .load(storage.getReference(user.picture))
                            .into(imgProfileMain)
                    }
                    else
                    {
                        imgProfileMain.setImageResource(R.drawable.icon_profile_pic)
                    }
                }
        }
        catch (ex : Exception)
        {
            Toast.makeText(this , ex.message , Toast.LENGTH_LONG).show()
        }




        btnSignOut.setOnClickListener {

            mAuthMain.signOut()

            val toSignInActivityIntent = Intent(this@MainActivity, SignInActivity::class.java)
            toSignInActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)  // to delete the previous activity(MainActivity)
            startActivity(toSignInActivityIntent)

        }



    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.icon_chats -> 
            {
                funSetFragment(mChats_Fragment)
                tv_toolbarTitle.text = "Chats"
                return true
            }
            
            R.id.icon_people ->
            {
                funSetFragment(mPeople_Fragment)
                tv_toolbarTitle.text = "People"
                return true
            }
            
            R.id.icon_more ->
            {
                funSetFragment(mMore_Fragment)
                tv_toolbarTitle.text = "More"
                return true
            }
            
            else -> return false
        }
    }

    private fun funSetFragment(mFrag: Fragment) {

        val fragTransaction = supportFragmentManager.beginTransaction()
        fragTransaction.replace(R.id.coordinator_Frag_Main , mFrag)
        fragTransaction.commit()

    }




    private fun funStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)  // M is sdk 23
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // it is to let status bar icons visible with the white color
        else
            window.statusBarColor = Color.WHITE  // to work with the smaller versions than sdk 23

        /*
        setSupportActionBar(toolbarMain)
        supportActionBar?.title = ""
        // i think we can ignore the 2 previous lines of action bar
        */
    }


}