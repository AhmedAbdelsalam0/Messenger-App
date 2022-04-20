package com.example.messenger.model

data class User (val name : String , val mail : String , val password : String , val picture : String)
{
    constructor() : this("","","" , "")  // it is to avoid exception because of getUserInfo function
}
