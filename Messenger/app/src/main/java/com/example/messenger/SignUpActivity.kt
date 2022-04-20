package com.example.messenger

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_sign_up.*


class SignUpActivity : AppCompatActivity() , TextWatcher{

    private val mAuth : FirebaseAuth by lazy {  // it is short way to initialize
        FirebaseAuth.getInstance()
    }

    private val dbInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    //private val currUserDocRef : DocumentReference
    //get() = dbInstance.document("users/${mAuth.currentUser?.uid}")

    private val currUserDocRef1 = dbInstance.collection("users").document(mAuth.currentUser?.uid.toString())  // instead of the first way

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)




        etName.addTextChangedListener(this@SignUpActivity)
        etMail.addTextChangedListener(this@SignUpActivity)
        etPassword.addTextChangedListener(this@SignUpActivity)


        btnSignUp.setOnClickListener {

            val name = etName.text.toString().trim()
            val mail = etMail.text.toString().trim()
            val password = etPassword.text.toString().trim()


            // some if conditions before connecting to the firebase

            if (name.isEmpty())
            {
                etName.error = "Name Required"
                etName.requestFocus()
                return@setOnClickListener  // to return first of function without continuing
            }

            if (mail.isEmpty())
            {
                etMail.error = "Email Required"
                etMail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches())  // to check if email is containing @ sign
            {
                etMail.error = "Not Valid Email"
                etMail.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6)
            {
                etPassword.error = "6 Characters or More Required"
                etPassword.requestFocus()
                return@setOnClickListener
            }

            funCreateNewAccount(name, mail, password )


        }

    }


    private fun funCreateNewAccount(name: String, mail: String, password: String ) {

        progUp.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener {

            //val newUser = hashMapOf("name" to name , "mail" to mail , "password" to password) // we can do any another ways than hash map

            val newUser = User(name , mail , password , picture = "" )
            currUserDocRef1.set(newUser)

            if (it.isSuccessful){
                progUp.visibility = View.INVISIBLE
                val toMainActivityIntent = Intent(this@SignUpActivity, MainActivity::class.java)
                toMainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)  // to delete the previous activity(signUp)
                startActivity(toMainActivityIntent)
            }
            else{
                progUp.visibility = View.INVISIBLE
                Toast.makeText(this@SignUpActivity, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }



    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        btnSignUp.isEnabled =
            etName.text.trim().isNotEmpty() &&
            etMail.text.trim().isNotEmpty()&&
            etPassword.text.trim().isNotEmpty()
    }

    override fun afterTextChanged(s: Editable?) {

    }
}