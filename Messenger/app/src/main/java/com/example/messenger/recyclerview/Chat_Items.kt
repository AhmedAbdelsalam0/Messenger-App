package com.example.messenger.recyclerview

import android.content.Context
import com.example.messenger.R
import com.example.messenger.model.User
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import glide.GlideApp
import kotlinx.android.synthetic.main.recycler_view_item.*
import java.security.AccessControlContext

class Chat_Items(val user : User , val context : Context) : Item() {

    private val storage : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }


    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.item_Name.text = user.name
        viewHolder.item_Time.text = "Time"
        viewHolder.item_LastMSG.text = "Last Message"


        if (user.picture.isNotEmpty())
        {
            GlideApp.with(context)
                .load(storage.getReference(user.picture))
                .into(viewHolder.item_Img)

        }
        else
        {
            viewHolder.item_Img.setImageResource(R.drawable.icon_profile_pic)
        }


    }

    override fun getLayout(): Int {


        return R.layout.recycler_view_item
    }


}