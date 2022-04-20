package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*


class SignInActivity : AppCompatActivity() , TextWatcher{

    private val mAuthIn : FirebaseAuth by lazy {  // it is short way to initialize
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        etMailIn.addTextChangedListener(this@SignInActivity)
        etPasswordIn.addTextChangedListener(this@SignInActivity)


        btnCreateNewAccount.setOnClickListener {

                val createNewAccountIntent = Intent(this@SignInActivity , SignUpActivity::class.java)
                startActivity(createNewAccountIntent)

        }


        btnSignIn.setOnClickListener {

            val mailIn = etMailIn.text.toString().trim()
            val passwordIn = etPasswordIn.text.toString().trim()


            // some if conditions before connecting to the firebase

            if (mailIn.isEmpty())
            {
                etMailIn.error = "Email Required"
                etMailIn.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(mailIn).matches())  // to check if email is containing @ sign
            {
                etMailIn.error = "Not Valid Email"
                etMailIn.requestFocus()
                return@setOnClickListener
            }

            if (passwordIn.length < 6)
            {
                etPasswordIn.error = "6 Characters or More Required"
                etPasswordIn.requestFocus()
                return@setOnClickListener
            }



            funSignIn(mailIn , passwordIn)


        }




    }


    private fun funSignIn(mailIn : String , passwordIn : String)
    {

        progIn.visibility = View.VISIBLE

        mAuthIn.signInWithEmailAndPassword(mailIn , passwordIn).addOnCompleteListener {

            if (it.isSuccessful){
                progIn.visibility = View.INVISIBLE
                val toMainActivityIntent = Intent(this@SignInActivity, MainActivity::class.java)
                toMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)  // to delete the previous activity(signIn)
                startActivity(toMainActivityIntent)
            }
            else{
                progIn.visibility = View.INVISIBLE
                Toast.makeText(this@SignInActivity, it.exception?.message, Toast.LENGTH_LONG).show()
            }

        }

    }


    override fun onStart() {
        super.onStart()

        if (mAuthIn.currentUser?.uid != null)  // if we didnt do it,, every time when user open the app ,, he will have to login
        {
            val toMainActivityIntent = Intent(this@SignInActivity, MainActivity::class.java)
            startActivity(toMainActivityIntent)
        }

    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        btnSignIn.isEnabled =
            etMailIn.text.trim().isNotEmpty()&&
            etPasswordIn.text.trim().isNotEmpty()

    }




}